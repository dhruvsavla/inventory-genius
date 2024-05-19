import React, { useState, useEffect, useMemo } from 'react';
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
import MyTable from './MyTable.js';
import 'jspdf-autotable';

const PicklistComponent = () => {
  const [apiData, setApiData] = useState([]);
  const [selectedRows, setSelectedRows] = useState([]);
  const [picklistData, setPicklistData] = useState([]);
  const [orderData, setOrderData] = useState([]);
  const [orders, setOrders] = useState([]);
  const [mergedOrderData, setMergedOrderData] = useState();
  const [mergedPicklistData, setMergedPicklistData] = useState([]); 
  const [selectedOrderData, setSelectedOrderData] = useState([]);

  useEffect (() => {
    axios.get('http://localhost:8080/picklists/orderData')
      .then(response => {
        setOrderData(response.data);
        console.log("orderData = " + JSON.stringify(orderData));
      })
      .catch(error => {
        console.error('Error getting order data:', error);
      });
  }, [])

  useEffect(() => {
    axios.get('http://localhost:8080/orders/not/generated/orders')
      .then(response => {
        setOrders(response.data);
      })
      .catch(error => {
        console.log("error getting orders: " + error);
      })
  }, [])

  useEffect(() => {
    axios.get('http://localhost:8080/picklists/merged/picklist')
      .then(response => {
        setPicklistData(response.data);
      })
      .catch(error => {
        console.error(error);
      });
  }, []);


  const handleCheckboxChange = (event, orderNo) => {
    console.log("in handle");
    
    if (orderNo === undefined) {
      // If orderNo is undefined, it means the "Select All" checkbox is clicked
      const allOrderNos = orders.map(order => order.orderNo);
      const updatedSelectedRows = event.target.checked ? allOrderNos : [];
      setSelectedRows(updatedSelectedRows);
  
      // Fetch order data for all selected orders
      if (event.target.checked) {
        // Create a promise chain to sequentially fetch order data for each selected order
        const promises = allOrderNos.map(orderNo => (
          axios.get(`http://localhost:8080/picklists/getSelectedOrderData?orderNo=${orderNo}`)
        ));

        console.log(event);
        console.log("Affirmative");
  
        // Execute promises sequentially using reduce
        promises.reduce((promiseChain, currentPromise) => {
          return promiseChain.then(chainResults =>
            currentPromise.then(currentResult =>
              [...chainResults, currentResult]
            )
          );
        }, Promise.resolve([]))
        .then(results => {
          // Concatenate order data from all promises into a single array
          const orderData = results.reduce((acc, response) => [...acc, ...response.data], []);
          // Set the fetched orderData to selectedOrderData state
          setSelectedOrderData(orderData);
        })
        .catch(error => {
          console.error('Error fetching order data:', error);
          // Handle error as needed
        });
      } else {
        // Clear selectedOrderData if no other individual checkboxes are checked
        if (selectedRows.length === 0 || (selectedRows.length === 1 )) {
          setSelectedOrderData([]);
        }
        console.log("Unselected");
      }
    }
   
  };
  
  
  const generatePicklist = () => {
    // Assuming your API endpoint for fetching all picklists is '/picklists'
    axios.get('http://localhost:8080/picklists')
      .then(response => {
        const picklists = response.data;
        if (picklists.length === 0) {
          // If no picklists are available, start with picklist number 1
          generatePicklistWithNumber(1);
          return;
        }
        // Sort picklists based on picklist number in descending order
        picklists.sort((a, b) => b.pickListNumber - a.pickListNumber);
        const latestPicklistNumber = picklists[0].pickListNumber;
        generatePicklistWithNumber(latestPicklistNumber + 1);
      })
      .catch(error => {
        console.error('Error fetching picklists:', error);
      });
  };
  
  const generatePicklistWithNumber = (pickListNumber) => {
    // Filter the entire order objects instead of just the items
    const selectedOrders = orders.filter(order => selectedRows.includes(order.orderNo));

    
    console.log("selected rows = " + selectedRows);

    // Assuming your API endpoint for generating a picklist is '/generate-picklist'
    axios.post('http://localhost:8080/picklists', { pickListNumber, orders: selectedOrders })
      .then(response => {
        console.log('Picklist generated successfully:', response.data);
        
        
        toast.success('PickList generated successfully', {
          autoClose: 2000 
        });
        // Optionally, you can update state or perform other actions here
      })
      .catch(error => {
        console.error('Error generating picklist:', error);
        toast.error('Failed to generate PickList: ' + error.message);
      });

  };


const selected = (selectedOrders) => {
  return selectedOrders.reduce((acc, picklist) => {
    console.log("selected orders = " + JSON.stringify(selectedOrders));
    const existingGroup = acc.find(group => group.sellerSKU === picklist.sellerSKU);
    if (existingGroup) {
      existingGroup.qty += picklist.qty; 
      existingGroup.pickQty += picklist.pickQty; // Accumulate pickQty
    } else {
      acc.push({ ...picklist });
    }
    return acc;
  }, []);
}

const generatePicklistPDF1 = async () => {
  try {
      // Create a new PDF instance
      const pdf = new jsPDF('p', 'mm', 'a4');
      const selectedOrders = orderData.filter(order => selectedRows.includes(order.orderNo));
      console.log(JSON.stringify(selectedOrders));

      // Group picklistData by pickListNumber
      const groupedData = selected(selectedOrders);
    
      // Define table columns
      const columns = [
          { title: "Date", dataKey: "date" },
          { title: "Order No", dataKey: "orderNo" },
          { title: "Portal", dataKey: "portal" },
          { title: "Seller SKU", dataKey: "sellerSKU" },
          { title: "Qty", dataKey: "qty" },
          { title: "Description", dataKey: "description" },
          { title: "Bin Number", dataKey: "binNumber" },
          { title: "Rack Number", dataKey: "rackNumber" },
          { title: "Pick Quantity", dataKey: "pickQty" }
      ];

      // Define table rows
      const rows = groupedData.map((order) => ({
          date: formatDate(order.date),
          orderNo: order.orderNo,
          portal: order.portal,
          sellerSKU: order.sellerSKU,
          qty: order.qty,
          description: order.description,
          binNumber: order.binNumber || 'N/A',
          rackNumber: order.rackNumber || 'N/A',
          pickQty: order.pickQty
      }));

      // Add table to PDF
      pdf.autoTable({ columns, body: rows });

      // Save the PDF with a specified name
      pdf.save('pick_list.pdf');

  } catch (error) {
      console.error("Error generating PDF:", error);
  }
};


const handleDelete = (id) => {
  console.log("Deleting row with id:", id);
  // Remove the row from the table

  axios.delete(`http://localhost:8080/picklists/${id}`)
  .then(response => {
    // Handle success response
    console.log('Row deleted successfully.');
    toast.success('PickList deleted successfully', {
      autoClose: 2000 // Close after 2 seconds
    });
    setPicklistData(prevData => prevData.filter(row => row.stockId !== id));

  })
  .catch(error => {
    // Handle error
    console.error('Error deleting row:', error);
    toast.error('Failed to delete picklist: ' + error.message);
  });

  console.log("After deletion, apiData:", apiData);
};

const handleRowClick = (event, orderNo) => {
  console.log("in handle1");

  // Check if the orderNo is already in selectedRows
  const isChecked = selectedRows.includes(orderNo);
  
  // Update selectedRows based on whether the orderNo is already selected or not
  const updatedSelectedRows = isChecked
    ? selectedRows.filter(id => id !== orderNo) // If already selected, remove it
    : [...selectedRows, orderNo]; // If not selected, add it
  
  // Update selectedRows state
  setSelectedRows(updatedSelectedRows);

  // If the row was just selected, fetch orderData for the selected orderNo
  if (!isChecked) {
    axios.get(`http://localhost:8080/picklists/getSelectedOrderData?orderNo=${orderNo}`)
      .then(response => {
        // Assuming the API response is an array of orderData
        const orderData = response.data;
        // Add the fetched orderData to selectedOrderData state
        setSelectedOrderData(prevData => [...prevData, ...orderData]);
      })
      .catch(error => {
        console.error('Error fetching order data:', error);
        // Handle error as needed
      });
  } else {
    // If the row was deselected, remove the corresponding orderData from selectedOrderData
    setSelectedOrderData(prevData => prevData.filter(order => order.orderNo !== orderNo));
  }
};


const handleDownload1 = async (pickListNumber) => {
  try {
      const picklistItems = picklistData.filter(picklist => picklist.pickListNumber === pickListNumber);
      
      if (picklistItems.length === 0) {
          console.error(`Picklist with pickListNumber ${pickListNumber} not found.`);
          return;
      }

      // Create a new PDF instance
      const pdf = new jsPDF();
      
      // Set font size and style for the table
      pdf.setFontSize(12);
      pdf.setFont('helvetica', 'bold');

      // Define table columns
      const columns = [
          { title: "Date", dataKey: "date" },
          { title: "Seller SKU", dataKey: "sellerSKU" },
          { title: "Order Qty", dataKey: "qty" },
          { title: "Pick Qty", dataKey: "pickQty" },
          { title: "Bin Number", dataKey: "binNumber" },
          { title: "Rack Number", dataKey: "rackNumber" }
      ];

      // Define table rows
      const rows = picklistItems.map(item => ({
          date: formatDate(item.date), // Format the date
          sellerSKU: item.sellerSKU,
          qty: item.qty,
          pickQty: item.pickQty,
          binNumber: item.binNumber || "N/A",
          rackNumber: item.rackNumber || "N/A"
      }));

      // Add table to PDF
      pdf.autoTable({ columns, body: rows });

      // Save the PDF with a specified name
      pdf.save(`picklist_${pickListNumber}.pdf`);
      
  } catch (error) {
      console.error("Error generating PDF:", error);
  }
};

// Function to format date as dd-mm-yyyy
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
          <h1>PickList</h1>
        </div>
        <Accordion defaultExpanded>
        <AccordionSummary
          expandIcon={<ExpandMoreIcon />}
          aria-controls="panel3-content"
          id="panel3-header"
          sx={{ backgroundColor: '#E5E7E9' }} 
        >
          <h4>PickList</h4>
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
        checked={selectedRows.length === orders.length} // Check if all rows are selected
        onChange={(event) => handleCheckboxChange(event)}
      />
    </th>
      <th>Date</th>
      <th>Order No</th>
      <th>Porta Order No</th>
      <th>Portal</th>
      <th>Seller SKU</th>
      <th>Order Qty</th>
      
    </tr>
  </thead>
  <tbody>
  {orders.map(order => (
    <tr key={uuidv4()} onClick={(event) => handleRowClick(event, order.orderNo)}>
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
      <td>{order.orderNo}</td>
      <td>{order.portalOrderNo}</td>
      <td>{order.portal}</td>
      <td>{order.sellerSKU}</td>
      <td>{order.qty}</td>
      
    </tr>
  ))}
</tbody>
</Table>


{selectedRows.length > 0 && (
  <MyTable 
    selectedOrderData={selectedOrderData} 
    generatePicklist={generatePicklist}
    generatePicklistPDF={generatePicklistPDF1}
  />
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
          <h4>List View of PickList</h4>
        </AccordionSummary>
        <AccordionDetails>
        
        <Table striped bordered hover>
  <thead>
    <tr>
      <th></th>
      <th></th>
      <th>Pick List Number</th>
      <th>Date</th>
      <th>Seller SKU</th>
      <th>Order Qty</th>
      <th>Pick Qty</th>
      <th>Bin Number</th>
      <th>Rack Number</th>
    </tr>
  </thead>
  <tbody>
    {picklistData.map((picklist, index) => {
      const rowsForPickListNumber = picklistData.filter(
        p => p.pickListNumber === picklist.pickListNumber
      ).length;

      const deleteButtonCell = index % rowsForPickListNumber === 0 ? (
        <td rowSpan={rowsForPickListNumber} style={{ width: '50px', textAlign: 'center' }}>
          <button
              style={{ display: 'inline-flex', justifyContent: 'center', alignItems: 'center', width: '100%', height: '100%', padding: '0', border: 'none', background: 'none' }}
              className="delete-icon"
              onClick={(e) => {
                e.stopPropagation(); 
                handleDelete(picklist.pickListId); 
              }}
            >
              <DeleteIcon style={{ color: '#F00' }} />
            </button>
        </td>
      ) : null;

      const downloadButtonCell = index % rowsForPickListNumber === 0 ? (
        <td rowSpan={rowsForPickListNumber}>
          <button onClick={() => handleDownload1(picklist.pickListNumber)}>Download</button>
        </td>
      ) : null;

      return (
        <tr key={`${picklist.pickListId}-${index}`}>
          {deleteButtonCell}
          {downloadButtonCell}
          {index % rowsForPickListNumber === 0 && <td rowSpan={rowsForPickListNumber}>{picklist.pickListNumber}</td>}
          <td>
            {(() => {
              const date = new Date(picklist.date);
              const day = String(date.getDate()).padStart(2, '0');
              const month = String(date.getMonth() + 1).padStart(2, '0');
              const year = date.getFullYear();
              return `${day}-${month}-${year}`;
            })()}
          </td>
          <td>{picklist.sellerSKU}</td>
          <td>{picklist.qty}</td>
          <td>{picklist.pickQty}</td>
          <td>{picklist.binNumber || "N/A"}</td>
          <td>{picklist.rackNumber || "N/A"}</td>
        </tr>
      );
    })}
  </tbody>
</Table>



      </AccordionDetails>
      </Accordion>
    </div>
  );
};

export default PicklistComponent;
