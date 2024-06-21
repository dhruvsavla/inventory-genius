import React, { useState, useEffect } from 'react';
import axios from 'axios';
import Table from 'react-bootstrap/Table';
import { v4 as uuidv4 } from 'uuid'; // Import UUID generator
import Accordion from '@mui/material/Accordion';
import AccordionSummary from '@mui/material/AccordionSummary';
import AccordionDetails from '@mui/material/AccordionDetails';
import ExpandMoreIcon from '@mui/icons-material/ExpandMore';
import "./PickList.css"
import { toast, ToastContainer } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import jsPDF from 'jspdf';
import html2canvas from 'html2canvas';
import DeleteIcon from '@mui/icons-material/Delete';
import Form from 'react-bootstrap/Form';
import Pagination from 'react-bootstrap/Pagination';
import SwapVertIcon from '@mui/icons-material/SwapVert';
import FileDownloadIcon from '@mui/icons-material/FileDownload';

const PicklistComponent = () => {
  const [apiData, setApiData] = useState([]);
  const [selectedRows, setSelectedRows] = useState([]);
  const [picklistData, setPicklistData] = useState([]);
  const [orderData, setOrderData] = useState([]);
  const [orders, setOrders] = useState([]);
  const rowsPerPageOptions = [10, 20, 50];
  const [currentPage, setCurrentPage] = useState(1);
  const [itemsPerPage, setItemsPerPage] = useState(10);
  const [sortConfig, setSortConfig] = useState({ key: null, direction: 'ascending' });
  const [selectedOrderData, setSelectedOrderData] = useState([]);

  // Function to handle change in items per page
  const handleItemsPerPageChange = (e) => {
    setItemsPerPage(parseInt(e.target.value));
    setCurrentPage(1); // Reset to first page when changing items per page
  };
  
  // JSX for the dropdown menu to select rows per page
  const rowsPerPageDropdown = (
    <Form.Group controlId="itemsPerPageSelect">
      <Form.Select style={{marginLeft: "5px", width : "70px"}} value={itemsPerPage} onChange={handleItemsPerPageChange}>
        {rowsPerPageOptions.map((option) => (
          <option key={option} value={option}>
            {option}
          </option>
        ))}
      </Form.Select>
    </Form.Group>
  );

  useEffect (() => {
    axios.get('http://localhost:8080/packinglist/orderData')
      .then(response => {
        setOrderData(response.data);
        console.log("orderData = " + JSON.stringify(orderData));
      })
      .catch(error => {
        console.error('Error getting order data:', error);
      });
  }, [])

  useEffect(() => {
    axios.get('http://localhost:8080/packinglist/not/generated/packinglist/orders')
      .then(response => {
        setOrders(response.data);
      })
      .catch(error => {
        console.log("error getting orders: " + error);
      })
  }, [])

  useEffect(() => {
    axios.get('http://localhost:8080/packinglist/getData')
      .then(response => {
        const mergedPicklistData = mergeRowsWithSamePicklist(response.data);
        setPicklistData(response.data);
        console.log("picklistData = " + JSON.stringify(picklistData));
      })
      .catch(error => {
        console.error(error);
      });
  }, []);

  const sortedData = picklistData.sort((a, b) => {
    if (sortConfig.key) {
      const aValue = a[sortConfig.key];
      const bValue = b[sortConfig.key];
      if (aValue < bValue) return sortConfig.direction === 'ascending' ? -1 : 1;
      if (aValue > bValue) return sortConfig.direction === 'ascending' ? 1 : -1;
      return 0;
    }
    return picklistData;
  });

  const requestSort = (key) => {
    let direction = 'ascending';
    if (sortConfig.key === key && sortConfig.direction === 'ascending') {
      direction = 'descending';
    }
    setSortConfig({ key, direction });
  };


  const handleCheckboxChange = async (event, itemId) => {
    if (itemId === undefined) {
      // If itemId is undefined, it means the checkbox in the header is clicked
      const allOrderNos = orders.map(order => order.orderNo);
      const updatedSelectedRows = event.target.checked ? allOrderNos : [];
      setSelectedRows(updatedSelectedRows);
  
      if (event.target.checked) {
        try {
          const promises = allOrderNos.map(orderNo =>
            axios.get(`http://localhost:8080/packinglist/selected/orderData?orderNo=${orderNo}`)
          );
          const results = await Promise.all(promises);
          const orderData = results.map(response => response.data);
          setSelectedOrderData(orderData);
        } catch (error) {
          console.error('Error fetching order data:', error);
        }
      } else {
        setSelectedOrderData([]);
      }
    } else {
      // If itemId is defined, it means a checkbox in a row is clicked
      const isChecked = event.target.checked;
      const updatedSelectedRows = isChecked
        ? [...selectedRows, itemId]
        : selectedRows.filter(id => id !== itemId);
      setSelectedRows(updatedSelectedRows);
  
      if (isChecked) {
        try {
          const response = await axios.get(`http://localhost:8080/packinglist/selected/orderData?orderNo=${itemId}`);
          const orderData = response.data;
          setSelectedOrderData(prevData => [...prevData, orderData]);
        } catch (error) {
          console.error('Error fetching order data:', error);
        }
      } else {
        setSelectedOrderData(prevData => prevData.filter(order => order.orderNo !== itemId));
      }
    }
  };
  

  
  const generatePicklist = () => {
    // Assuming your API endpoint for fetching all picklists is '/picklists'
    axios.get('http://localhost:8080/packinglist')
      .then(response => {
        const picklists = response.data;
        if (picklists.length === 0) {
          // If no picklists are available, start with picklist number 1
          generatePicklistWithNumber(1);
          return;
        }
        // Sort picklists based on picklist number in descending order
        picklists.sort((a, b) => b.packingListNumber - a.packingListNumber);
        const latestPicklistNumber = picklists[0].packingListNumber;
        generatePicklistWithNumber(latestPicklistNumber + 1);
      })
      .catch(error => {
        console.error('Error fetching picklists:', error);
      });
  };
  
  const generatePicklistWithNumber = (packingListNumber) => {
    // Filter the entire order objects instead of just the items
    const selectedOrders = orders.filter(order => selectedRows.includes(order.orderNo));
  
    console.log("selected rows = " + selectedRows);
    console.log("selected order data = " + JSON.stringify(selectedOrderData));
  
    // Map the selected order data to the desired format
const selectedOrder = selectedOrderData[0].map(order => ({
  packListNumber: packingListNumber,
  date: order.date,
  portalOrderNo: order.portalOrderNo,
  orderNo: order.orderNo,
  bomCode: order.bomCode,
  portal: order.portal,
  sellerSKU: order.sellerSKU,
  qty: order.qty,
  description: order.description,
  packQty: order.pickQty
}));

// Loop through each selected order and post it individually
selectedOrder.forEach(order => {
  axios.post('http://localhost:8080/packinglistdata', order)
    .then(response => {
      console.log("after post" + JSON.stringify(response.data));

      setSelectedOrderData([]);
      setSelectedRows([]);

      axios.get('http://localhost:8080/packinglist/not/generated/packinglist/orders')
      .then(response => {
        setOrders(response.data);
      })
      .catch(error => {
        console.log("error getting orders: " + error);
      })

      axios.get('http://localhost:8080/packinglist/getData')
      .then(response => {
        const mergedPicklistData = mergeRowsWithSamePicklist(response.data);
        setPicklistData(response.data);
        console.log("picklistData = " + JSON.stringify(picklistData));
      })
      .catch(error => {
        console.error(error);
      });
    })
    .catch(error => {
      console.error('Error generating picklist data:', error);
    });
});

    // Assuming your API endpoint for generating a picklist is '/generate-picklist'
    axios.post('http://localhost:8080/packinglist', { packingListNumber, orders: selectedOrders })
      .then(response => {
        console.log('Picklist generated successfully:', response.data);
        toast.success('PickList generated successfully', {
          autoClose: 2000 // Close after 2 seconds
        });
        // Optionally, you can update state or perform other actions here
      })
      .catch(error => {
        console.error('Error generating picklist:', error);
        toast.error('Failed to generate PickList: ' + error.message);
      });

      
  };
  


  const generatePicklistPDF = async () => {
    // Get the selected orders based on the selectedRows
    const selectedOrders = apiData.filter(order => selectedRows.includes(order.orderId));

    // Pre-load images
    const imagePromises = selectedOrders.flatMap(order => order.items.map(item => item.img)).map(src => {
        return new Promise(resolve => {
            const image = new Image();
            image.onload = () => resolve(image);
            image.src = src;
        });
    });

    const images = await Promise.all(imagePromises);

    // Get the table element to convert it into a PDF
    const table = document.querySelector('.picklist-table');

    // Use html2canvas to capture the HTML content of the table
    html2canvas(table, { scale: 2 }) // Scale up the canvas to improve text quality
        .then(canvas => {
            // Convert the captured content into a PDF using jspdf
            const pdf = new jsPDF('p', 'mm', 'a4');
            const imgWidth = 210; // A4 width in mm
            const imgHeight = canvas.height * imgWidth / canvas.width;
            let yPos = 10; // Initial Y position

            // Add images and text to the PDF
            selectedOrders.forEach((order, index) => {
                const image = images[index];
                const remainingHeight = pdf.internal.pageSize.height - yPos; // Calculate remaining height on the page
                const spaceNeeded = 80; // Height required for each item (image + text) - adjust as needed

                if (remainingHeight < spaceNeeded) {
                    pdf.addPage(); // Start a new page if there's not enough space
                    yPos = 10; // Reset Y position for the new page
                }

                pdf.addImage(image, 'PNG', 10, yPos, 50, 50); // Add image

                // Set font size
                pdf.setFontSize(10); // Adjust the font size as needed

                // Add text with reduced font size and line spacing
                pdf.text(`Date: ${order.date}`, 70, yPos + 5); // Add date
                pdf.text(`Order No: ${order.orderNo}`, 70, yPos + 15); // Add order number
                pdf.text(`Portal: ${order.portal}`, 70, yPos + 25); // Add portal (reduced line spacing)
                pdf.text(`Seller SKU: ${order.sellerSKU}`, 70, yPos + 35); // Add seller SKU (reduced line spacing)
                pdf.text(`Qty: ${order.qty}`, 70, yPos + 45); // Add quantity (reduced line spacing)
                pdf.text(`Description: ${order.items[0].description}`, 70, yPos + 55); // Add description (reduced line spacing)
                pdf.text(`Bin Number: ${order.items[0].storage ? order.items[0].storage.binNumber || "NA" : "NA"}`, 70, yPos + 65); // Add bin number (reduced line spacing)
                pdf.text(`Rack Number: ${order.items[0].storage ? order.items[0].storage.rackNumber || "NA" : "NA"}`, 70, yPos + 75); // Add rack number (reduced line spacing)

                // Draw a horizontal line at the bottom of each order section
                pdf.line(10, yPos + spaceNeeded - 5, 200, yPos + spaceNeeded - 5);

                yPos += spaceNeeded; // Update Y position for the next item
            });

            // Save the PDF with a specified name
            pdf.save('pick_list.pdf');
        });
};

const handleDelete = (packingListNumber) => {
  console.log("Deleting row with packingListNumber:", packingListNumber);

  // Make DELETE request to remove the row from the database
  axios.delete(`http://localhost:8080/packinglistdata/packinglistData/${packingListNumber}`)
    .then(response => {
      // Handle success response
      console.log('Row deleted successfully.');
      toast.success('PackingList deleted successfully', {
        autoClose: 2000 // Close after 2 seconds
      });

      // Update the picklistData state to remove the deleted row
      setPicklistData(prevData => prevData.filter(row => row.packingListNumber !== packingListNumber));
      axios.get('http://localhost:8080/packinglist/getData')
      .then(response => {
        const mergedPicklistData = mergeRowsWithSamePicklist(response.data);
        setPicklistData(response.data);
        console.log("picklistData = " + JSON.stringify(picklistData));
      })
      .catch(error => {
        console.error(error);
      });
      // Fetch updated orders
      return axios.get('http://localhost:8080/packinglist/not/generated/packinglist/orders');
    })
    .then(response => {
      // Update the orders state with the new data
      setOrders(response.data);
    })
    .catch(error => {
      // Handle error
      console.error('Error deleting row:', error);
      toast.error('Failed to delete PackingList: ' + error.message);
    });

  console.log("After deletion, apiData:", apiData);
};


// const handleRowClick = (event, order) => {
//   const isChecked = selectedRows.includes(order.orderNo);
//   const updatedSelectedRows = isChecked
//     ? selectedRows.filter(id => id !== order.orderNo)
//     : [...selectedRows, order.orderNo];

//   setSelectedRows(updatedSelectedRows);

// };


const handleDownload = async (pickListNumber) => {
  console.log("pickData = " + JSON.stringify(picklistData));
  try {
    const orders = picklistData.filter(order => order.pickListNumber === pickListNumber);
    console.log("Found Orders:", orders);

    if (orders.length === 0) {
      console.error(`Orders with pickListNumber ${pickListNumber} not found.`);
      return;
    }

    // Create a new PDF instance
    const pdf = new jsPDF('p', 'mm', 'a4');
    let yPos = 10; // Initial Y position

    // Set font size
    pdf.setFontSize(10); // Adjust the font size as needed

    // Define the vertical spacing between orders

    const spacingBetweenOrders = 160; // Adjust as needed
orders.forEach((order, index) => {
    // Calculate the Y position for the current order
    const currentYPos = yPos + index * spacingBetweenOrders;

    // Add text for each order with appropriate spacing
    pdf.text(`Date: ${order.date}`, 10, currentYPos + 10); // Add date
    pdf.text(`Portal: ${order.portal}`, 10, currentYPos + 20); // Add portal
    pdf.text(`Portal OrderNo : ${order.portalOrderNo}`, 10, currentYPos + 35)
    pdf.text(`Qty: ${order.qty}`, 10, currentYPos + 50); // Add quantity
    pdf.text(`Description: ${order.description}`, 10, currentYPos + 65); // Add description
    pdf.text(`Pack Quantity: ${order.pickQty}`, 10, currentYPos + 80); // Add pick quantity

    // Draw a horizontal line at the bottom of each order section
    pdf.line(10, currentYPos + 95, 200, currentYPos + 95);
});

    // Save the PDF with a specified name
    pdf.save(`pack_list_${pickListNumber}.pdf`);

  } catch (error) {
    console.error("Error generating PDF:", error);
  }
};

const indexOfLastItem = currentPage * itemsPerPage;
  const indexOfFirstItem = indexOfLastItem - itemsPerPage;
  const currentItems = picklistData.slice(indexOfFirstItem, indexOfLastItem);

  const paginate = (pageNumber) => setCurrentPage(pageNumber);

const handleDownload1 = async (pickListNumber) => {
  try {
      const picklistItems = picklistData.filter(picklist => picklist.pickListNumber === pickListNumber);
      
      if (picklistItems.length === 0) {
          console.error(`Picklist with pickListNumber ${pickListNumber} not found.`);
          return;
      }

      // Create a new PDF instance
      const pdf = new jsPDF();

      // Add a company logo (example logo URL)
      const logoURL = 'https://media.licdn.com/dms/image/D560BAQF6CchqkqZEEQ/company-logo_200_200/0/1704887637105/techjyot___india_logo?e=2147483647&v=beta&t=S1jLov5GABl39n8XPksGcm8GIQsmvMTLl84RwYZNL-8'; // Replace with your actual base64 or URL
      pdf.addImage(logoURL, 'PNG', 10, 10, 30, 15); // Adjust the position and size as needed

      // Add heading
      pdf.setFontSize(18);
      pdf.setFont('helvetica', 'bold');
      pdf.text('Packinglist', pdf.internal.pageSize.getWidth() / 2, 20, { align: 'center' });

      // Add current date
      const currentDate = new Date();
      const formattedDate = `${currentDate.getDate()}/${currentDate.getMonth() + 1}/${currentDate.getFullYear()}`;
      pdf.setFontSize(12);
      pdf.setFont('helvetica', 'normal');
      pdf.text(formattedDate, pdf.internal.pageSize.getWidth() - 50, 20);

      // Add PackingList Number
      pdf.setFontSize(14);
      pdf.setFont('helvetica', 'normal');
      pdf.text(`PackingList Number: ${pickListNumber}`, 15, 35);

      // Define table columns
      const columns = [
          {title: "Date", dataKey: "date"},
          { title: "Portal", dataKey: "portal" },
          { title: "Portal Order No", dataKey: "portalOrderNo" },
          { title: "Order Qty", dataKey: "qty" },
          { title: "Pack Qty", dataKey: "pickQty" },
      ];

      // Define table rows with merged data
      const rows = picklistItems.map(item => ({
          date: formatDate(item.date),
          portal: item.portal,
          portalOrderNo: item.portalOrderNo,
          qty: item.qty,
          pickQty: item.pickQty,
      }));

      // Add table to PDF
      pdf.autoTable({ columns, body: rows, startY: 40 }); // Start table after the header

      // Save the PDF with a specified name
      pdf.save(`packlist_${pickListNumber}.pdf`);
      
  } catch (error) {
      console.error("Error generating PDF:", error);
  }
};



const generatePicklistPDF1 = async () => {
  try {
      // Create a new PDF instance
      const pdf = new jsPDF('p', 'mm', 'a4');
      const selectedOrders = orderData.filter(order => selectedRows.includes(order.orderNo));
      console.log(JSON.stringify(selectedOrders));

      // Group picklistData by pickListNumber
    
      // Define table columns
      const columns = [
          { title: "Date", dataKey: "date" },
          { title: "Portal", dataKey: "portal" },
          { title: "Qty", dataKey: "qty" },
          { title: "Description", dataKey: "description" },
          { title: "Pack Quantity", dataKey: "pickQty" },

        ];

      // Define table rows
      const rows = selectedOrders.map((order) => ({
          date: formatDate(order.date),
          portal: order.portal,
          qty: order.qty,
          description: order.description,
          pickQty: order.pickQty,
      }));

      // Add table to PDF
      pdf.autoTable({ columns, body: rows });

      // Save the PDF with a specified name
      pdf.save('pack_list.pdf');

  } catch (error) {
      console.error("Error generating PDF:", error);
  }
};


const formatDate = (dateString) => {
  const date = new Date(dateString);
  const day = String(date.getDate()).padStart(2, '0');
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const year = date.getFullYear();
  return `${day}-${month}-${year}`;
};

  return (
    <div>
      <div className='title'>
          <h1>Packing List</h1>
        </div>
      <Accordion defaultExpanded>
        <AccordionSummary
          expandIcon={<ExpandMoreIcon />}
          aria-controls="panel3-content"
          id="panel3-header"
          sx={{ backgroundColor: '#E5E7E9' }} 
        >
          <h4>PackingList</h4>
        </AccordionSummary>
        <AccordionDetails>
        <ToastContainer position="top-right" />
       
        <Table striped bordered hover className='custom-table'>
  <thead>
    <tr>
    <th>
      <input
        type="checkbox"
        id={`checkbox-${uuidv4()}`}
        checked={selectedRows.length === orderData.length} // Check if all rows are selected
        onChange={(event) => handleCheckboxChange(event)}
      />
    </th>
      <th>Date</th>
      <th>Portal</th>
      <th>Order Qty</th>
      <th>Description</th>
    </tr>
  </thead>
  <tbody>
  {orders.map(order => (
    // <tr key={uuidv4()} onClick={(event) => handleRowClick(event, order.orderNo)}>
    <tr>
      <td>
        <input
          type="checkbox"
          id={`checkbox-${uuidv4()}`}
          checked={selectedRows.includes(order.orderNo)}
          onChange={(event) => handleCheckboxChange(event, order.orderNo)}
        />
      </td>
      <td>
        {(() => {
          const date = new Date(order.date);
          const day = String(date.getDate()).padStart(2, '0');
          const month = String(date.getMonth() + 1).padStart(2, '0');
          const year = date.getFullYear();
          return `${day}-${month}-${year}`;
        })()}
      </td>
      <td>{order.portal}</td>
      <td>{order.qty}</td>
      <td>{order.items[0].description}</td>
    </tr>
  ))}
</tbody>
</Table>


{selectedRows.length > 0 && (
  <Table striped bordered hover className='custom-table picklist-table'>
    <thead>
      <tr>
        <th>Date</th>
        <th>Portal</th>
        <th>Order Qty</th>
        <th>Description</th>
        <th>Pack Qty</th>
        <th>Image</th>
      </tr>
    </thead>
    <tbody>
    {orderData.filter(order => selectedRows.includes(order.orderNo)).map(order =>
            <tr key={uuidv4()}>
             <td>
                {(() => {
                  const date = new Date(order.date);
                  const day = String(date.getDate()).padStart(2, '0');
                  const month = String(date.getMonth() + 1).padStart(2, '0');
                  const year = date.getFullYear();
                  return `${day}-${month}-${year}`;
                })()}
              </td>
              <td>{order.portal}</td>
              <td>{order.qty}</td>
              <td>{order.description}</td>
              <td>{order.pickQty}</td>
              <td><img src = {order.img} style = {{height: "200px"}}></img></td>
            </tr>
      )}
      
    </tbody>
   
  <button className = "generateButton" onClick={generatePicklist}>Generate PackingList</button>
  <button className="generateButton" onClick={generatePicklistPDF1}>Download PackingList</button>

  </Table>
  
)}
</AccordionDetails>
      </Accordion>



<Accordion>
        <AccordionSummary
          expandIcon={<ExpandMoreIcon />}
          aria-controls="panel3-content"
          id="panel3-header"
          sx={{ backgroundColor: '#E5E7E9' }} 
        >
          <h4>List View of PackingList</h4>
        </AccordionSummary>
        <AccordionDetails>

        <Table striped bordered hover>
  <thead>
    <tr>
      <th></th>
      <th></th>
      <th>
      <SwapVertIcon style = {{cursor: 'pointer', marginRight: "2%"}}variant="link" onClick={() => requestSort('pickListNumber')}>
                  </SwapVertIcon>
        Pack List Number</th>
      <th>
      <SwapVertIcon style = {{cursor: 'pointer', marginRight: "2%"}}variant="link" onClick={() => requestSort('date')}>
                  </SwapVertIcon>
        Date</th>
      <th>
      <SwapVertIcon style = {{cursor: 'pointer', marginRight: "2%"}}variant="link" onClick={() => requestSort('portalOrderNo')}>
                  </SwapVertIcon>
        Portal Order No</th>
      <th>
      <SwapVertIcon style = {{cursor: 'pointer', marginRight: "2%"}}variant="link" onClick={() => requestSort('portal')}>
                  </SwapVertIcon>
        Portal</th>
      <th>
      <SwapVertIcon style = {{cursor: 'pointer', marginRight: "2%"}}variant="link" onClick={() => requestSort('qty')}>
                  </SwapVertIcon>
        Order Qty</th>
      <th>
      <SwapVertIcon style = {{cursor: 'pointer', marginRight: "2%"}}variant="link" onClick={() => requestSort('pickQty')}>
                  </SwapVertIcon>
        Pack Qty</th>
    </tr>
  </thead>
  <tbody>
    {picklistData.map((picklist, index) => {
      const rowspan = picklistData.filter(p => p.packListNumber === picklist.packListNumber).length;
      return (
        <tr key={`${picklist.pickListId}-${index}`}>
          {index === 0 || picklist.packListNumber !== picklistData[index - 1].packListNumber ? (
            <>
              <td rowSpan={rowspan} style={{ width: '50px', textAlign: 'center' }}>
                <button
                  style={{ display: 'inline-flex', justifyContent: 'center', alignItems: 'center', width: '100%', height: '100%', padding: '0', border: 'none', background: 'none' }}
                  className="delete-icon"
                  onClick={(e) => {
                    e.stopPropagation(); 
                    handleDelete(picklist.packListNumber); 
                  }}
                >
                  <DeleteIcon style={{ color: '#F00' }} />
                </button>
              </td>
              {index === 0 || picklist.packListNumber !== picklistData[index - 1].packListNumber ? (
            <td rowSpan={rowspan}>
              <button onClick={() => handleDownload1(picklist.packListNumber)}>Download</button>
            </td>
          ) : null}
              <td rowSpan={rowspan}>{picklist.packListNumber}</td>
            </>
          ) : null}
          
          <td>
            {(() => {
              const date = new Date(picklist.date);
              const day = String(date.getDate()).padStart(2, '0');
              const month = String(date.getMonth() + 1).padStart(2, '0');
              const year = date.getFullYear();
              return `${day}-${month}-${year}`;
            })()}
          </td>
          <td>{picklist.order.portalOrderNo}</td>
          <td>{picklist.order.itemPortalMapping.portal}</td>
          <td>{picklist.qty}</td>
          <td>{picklist.packQty}</td>
        </tr>
      );
    })}
  </tbody>
</Table>

<div style={{ display: 'flex', justifyContent: 'space-between' }}>
            {rowsPerPageDropdown}
            <Pagination>
              {Array.from({ length: Math.ceil(picklistData.length / itemsPerPage) }).map((_, index) => (
                <Pagination.Item key={index} active={index + 1 === currentPage} onClick={() => paginate(index + 1)}>
                  {index + 1}
                </Pagination.Item>
              ))}
            </Pagination>
          </div>
      </AccordionDetails>
      </Accordion>
    </div>
  );
};

export default PicklistComponent;

function mergeRowsWithSamePicklist(data) {
  const mergedData = [];
  data.forEach(picklist => {
    const existingPicklist = mergedData.find(item => item.packingListNumber === picklist.packingListNumber);
    if (existingPicklist) {
      //existingPicklist.picklist = existingPicklist.picklist.concat(picklist.picklist);
    } else {
      mergedData.push({ ...picklist });
    }
  });
  return mergedData;
}