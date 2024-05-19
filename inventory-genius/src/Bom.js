import React, { useState, useEffect } from 'react';
import Button from 'react-bootstrap/Button';
import Col from 'react-bootstrap/Col';
import Form from 'react-bootstrap/Form';
import Row from 'react-bootstrap/Row';
import Table from 'react-bootstrap/Table';
import 'bootstrap/dist/css/bootstrap.min.css';
import './Item.css';
import Header from './Header';
import Accordion from '@mui/material/Accordion';
import AccordionSummary from '@mui/material/AccordionSummary';
import AccordionDetails from '@mui/material/AccordionDetails';
import ExpandMoreIcon from '@mui/icons-material/ExpandMore';
import axios from 'axios'; 
import TextField from '@mui/material/TextField';
import Autocomplete from '@mui/material/Autocomplete';
import * as XLSX from 'xlsx';
import DeleteIcon from '@mui/icons-material/Delete';
import { saveAs } from 'file-saver';
import { Link } from 'react-router-dom';
import { IoIosRefresh } from "react-icons/io";
import { toast, ToastContainer } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';

function Bom() {
  const [validated, setValidated] = useState(false);
  const [skucode, setSku] = useState("");
  const [bomItem, setBomItem] = useState("");
  const [qty, setQty] = useState("");
  const [apiData, setApiData] = useState([]); 
  const [rowSelected, setRowSelected] = useState(false);
  const [selectedItem, setSelectedItem] = useState(null);
  const [searchTerm, setSearchTerm] = useState("");
  const [skuList, setSkuList] = useState([]);
  const [skuSearchTerm, setSkuSearchTerm] = useState("");
  const [bomItemSearchTerm, setBomItemSearchTerm] = useState("");
  const [qtySearchTerm, setQtySearchTerm] = useState("");
  const [isRotating, setIsRotating] = useState(false);

  const handleRefresh = () => {
    fetchData();
    setIsRotating(true);
    setTimeout(() => {
      setIsRotating(false);
    }, 1000);
  };

  const filteredData = apiData.filter(supplier => {
    return (
      (supplier.qty && supplier.qty.toString().toLowerCase().includes(qtySearchTerm.toLowerCase())) && 
      (supplier.bomItem && supplier.bomItem.toLowerCase().includes(bomItemSearchTerm.toLowerCase())) &&
      (supplier.skucode && supplier.skucode.toLowerCase().includes(skuSearchTerm.toLowerCase()))
    );
  });
  
  const fetchData = () => {
    axios.get('http://localhost:8080/item/supplier') // Fetch SKU codes and descriptions from the items table
      .then(response => {
        // Extract SKU codes and descriptions from the response data and filter out null or undefined values
        const skuData = response.data
          .filter(item => item.skucode && item.description) // Filter out items where skucode or description is null or undefined
          .map(item => ({ skucode: item.skucode, description: item.description }));
        // Set the SKU data list state
        setSkuList(skuData);
      })
      .catch(error => console.error(error));
  }

  useEffect(() => {
    axios.get('http://localhost:8080/item/supplier') // Fetch SKU codes and descriptions from the items table
      .then(response => {
        // Extract SKU codes and descriptions from the response data and filter out null or undefined values
        const skuData = response.data
          .filter(item => item.skucode && item.description) // Filter out items where skucode or description is null or undefined
          .map(item => ({ skucode: item.skucode, description: item.description }));
        // Set the SKU data list state
        setSkuList(skuData);
      })
      .catch(error => console.error(error));
  }, []);

  const postData = (data) => {
    axios.post('http://localhost:8080/boms', data)
        .then(response => {
            // Handle successful response
            console.log('Data posted successfully:', response);
        })
        .catch(error => {
            // Handle error
            console.error('Error posting data:', error);
        });
};

const handleFileUpload = (e) => {
    const file = e.target.files[0];
    const reader = new FileReader();

    reader.onload = (evt) => {
        const data = evt.target.result;
        const workbook = XLSX.read(data, { type: 'binary' });
        const sheetName = workbook.SheetNames[0];
        const sheet = workbook.Sheets[sheetName];
        const jsonData = XLSX.utils.sheet_to_json(sheet);

        //jsonData.shift();

        jsonData.forEach(item => {
            const formattedData = {
                bomItem: item.bomItem,
                qty: item.qty,
                skucode: item.skucode
            };

            // Fetch item details using skucode
            axios.get(`http://localhost:8080/item/supplier/search/skucode/${item.skucode}`)
                .then(response => {
                    // Check if item exists
                    if (response.data.length === 0) {
                        console.error('Item not found with SKU code: ' + item.skucode);
                        return;
                    }

                    // Extract item from response data
                    const fetchedItem = response.data;

                    // Construct formData with fetched item
                    const formData = {
                        ...formattedData,
                        bomItems: [fetchedItem] // Wrap the single item in an array
                    };

                    console.log('Form data:', formData);

                    // Send data to server
                    postData(formData);
                })
                .catch(error => {
                    console.error('Error fetching item:', error);
                });
        });
    };

    reader.readAsBinaryString(file);
};
  

const handleSubmit = (event) => {
  console.log("qty = ", qty);
  event.preventDefault();
  const form = event.currentTarget;

  if (form.checkValidity() === false || !bomItem || !qty || !skucode) {
    event.stopPropagation();
    setValidated(true); 
    return;
  } else {
    // Fetch item details using skucode
    axios.get(`http://localhost:8080/item/supplier/search/skucode/${skucode}`)
      .then(response => {
        console.log("item = " + JSON.stringify(response.data));
        // Check if item exists
        if (response.data.length === 0) {
          toast.error('Item not found with SKU code: ' + skucode);
          return;
        }

        // Extract item from response data
        const item = response.data;

        // Construct formData with item
        const formData = {
          skucode, 
          bomItem,
          bomItems : [item], // Wrap the single item in an array
          qty,
        };

        // Send POST request with formData
        axios.post('http://localhost:8080/boms' , formData)
          .then(response => {
            console.log('POST request successful:', response);
            toast.success('BOM added successfully', {
              autoClose: 2000 // Close after 2 seconds
            });
            console.log('form data: ', formData);
            setValidated(false);
            setApiData([...apiData, response.data]);
            setBomItem("");
            setQty("");
            setSku("");
          })
          .catch(error => {
            console.error('Error sending POST request:', error);
            toast.error('Failed to add BOM: ' + error.response.data.message);
          });
      })
      .catch(error => {
        console.error('Error fetching item:', error);
        toast.error('Failed to fetch item details: ' + error.response.data.message);
      });
  }

  setValidated(true);
};



const handleRowSubmit = () => {
  console.log("handleRowSubmit triggered");
  console.log(selectedItem);
  if (rowSelected && selectedItem) {
    // Fetch item details using skucode
    axios.get(`http://localhost:8080/item/supplier/search/skucode/${skucode}`)
      .then(response => {
        // Check if item exists
        if (response.data.length === 0) {
          toast.error('Item not found with SKU code: ' + skucode);
          return;
        }

        // Extract item from response data
        const item = response.data;

        // Construct formData with item
        const formData = {
          skucode,
          bomItem,
          bomItems: [item], // Wrap the single item in an array
          qty
        };

        console.log('form data: ', formData);
        console.log("id: ", selectedItem.bomId);

        axios.put(`http://localhost:8080/boms/${selectedItem.bomId}`, formData)
          .then(response => {
            console.log('PUT request successful:', response);
            toast.success('Bom updated successfully', {
              autoClose: 2000 // Close after 2 seconds
            });
            setApiData(prevData => prevData.map(item => item.bomId === selectedItem.bomId ? response.data : item)); // Update the specific item

            setValidated(false);
            setRowSelected(false);
            setSku("");
            setBomItem("");
            setQty("");
          })
          .catch(error => {
            console.error('Error sending PUT request:', error);
            toast.error('Failed to update BOM: ' + error.response.data.message);
          });
      })
      .catch(error => {
        console.error('Error fetching item:', error);
        toast.error('Failed to fetch item details: ' + error.response.data.message);
      });
  }
};

  
  const handleRowClick = (bom) => {
    setSku(bom.skucode);
    setBomItem(bom.bomItem);
    setQty(bom.qty);
    setRowSelected(true);
    setSelectedItem(bom);
  };

  const handleSearchChange = (event) => {
    setSearchTerm(event.target.value.toLowerCase()); // Convert search term to lowercase
  };
  
  useEffect(() => {
    axios.get('http://localhost:8080/boms') 
      .then(response => setApiData(response.data))
      .catch(error => console.error(error));
    
  }, []);

const downloadTemplate = () => {
  const templateData = [
      {bomItem: '', qty: '', skucode: ''} 
  ];
  const ws = XLSX.utils.json_to_sheet(templateData);
  const wb = XLSX.utils.book_new();
  XLSX.utils.book_append_sheet(wb, ws, 'Template');
  const wbout = XLSX.write(wb, { bookType: 'xlsx', type: 'binary' });

  function s2ab(s) {
      const buf = new ArrayBuffer(s.length);
      const view = new Uint8Array(buf);
      for (let i = 0; i !== s.length; ++i) view[i] = s.charCodeAt(i) & 0xFF;
      return buf;
  }

  saveAs(new Blob([s2ab(wbout)], { type: 'application/octet-stream' }), 'bomTemplate.xlsx');
};

const handleDelete = (id) => {
  console.log("Deleting row with id:", id);
  // Remove the row from the table

  axios.delete(`http://localhost:8080/boms/${id}`)
  .then(response => {
    // Handle success response
    console.log('Row deleted successfully.');
    toast.success('Bom deleted successfully', {
      autoClose: 2000 // Close after 2 seconds
    });
    setApiData(prevData => prevData.filter(row => row.bomId !== id));

  })
  .catch(error => {
    // Handle error
    console.error('Error deleting row:', error);
    toast.error('Failed to delete BOM: ' + error.response.data.message);
  });


  console.log("After deletion, apiData:", apiData);
};
  

  return (
    <div>
      <ToastContainer position="top-right" />
      <div className='title'>
        <h1>BOM</h1>
      </div>

      
      <Accordion defaultExpanded>
        <AccordionSummary className='acc-summary'
          expandIcon={<ExpandMoreIcon />}
          aria-controls="panel3-content"
          id="panel3-header"
          sx={{ backgroundColor: '#E5E7E9' }} 
        >
          <h4>BOM Form</h4>
        </AccordionSummary>
        <AccordionDetails>
        <Form noValidate validated={validated} onSubmit={handleSubmit}>
            <Row className="mb-3">
              <Form.Group as={Col} md="4" controlId="validationCustom02">
                <Form.Label>BOM item</Form.Label>
                <Form.Control
                  required
                  type="text"
                  placeholder="BOM item"
                  name="bomItem"
                  value={bomItem}
                  onChange={(e) => setBomItem(e.target.value)}
                />
                <Form.Control.Feedback>Looks good!</Form.Control.Feedback>
              </Form.Group>
              <Form.Group as={Col} md="4" controlId="validationCustom03">
                <Form.Label>Quantity</Form.Label>
                <Form.Control
                  required
                  type="text"
                  placeholder="Quantity"
                  name="quantity"
                  value={qty}
                  onChange={(e) => setQty(e.target.value)}
                />
                <Form.Control.Feedback>Looks good!</Form.Control.Feedback>
              </Form.Group>
              <Form.Group as={Col} md="4" controlId="validationCustom01">
                <Form.Label>SKU Code</Form.Label>

                  <Form.Select
                    required
                    onChange={(e) => setSku(e.target.value)}
                    value={skucode}
                  >
                    <option value="">Select SKU Code</option>
                    {skuList.map((sku) => (
                      <option key={sku.id} value={sku.skucode}>
                        {sku.skucode} - {sku.description}
                      </option>
                    ))}
                  </Form.Select>
                  <Link to="/Item" target="_blank"><span style = {{float:"right", fontSize:"small", marginTop:"1%", marginRight:"1%"}}>+ add item</span></Link>
          <IoIosRefresh onClick={handleRefresh} className={isRotating ? 'refresh-icon rotating' : 'refresh-icon'}/>
                  <Form.Control.Feedback>Looks good!</Form.Control.Feedback>

              </Form.Group>
            </Row>
            <div className='buttons'>
      {rowSelected ? (
        <Button onClick={handleRowSubmit}>Edit</Button>
      ) : (
        <Button type="submit" onClick={handleSubmit}>Submit</Button>
      )}
      <span style={{ margin: '0 10px' }}>or</span>
            <input type="file" onChange={handleFileUpload} />
            <button onClick={downloadTemplate}>Download Template</button>

            </div>
          </Form>
        </AccordionDetails>
      </Accordion>
      
      <Accordion>
        <AccordionSummary
          expandIcon={<ExpandMoreIcon />}
          aria-controls="panel3-content"
          id="panel3-header"
          sx={{ backgroundColor: '#E5E7E9' }} 
        >
          <h4>List View of Bom</h4>
        </AccordionSummary>
        <AccordionDetails>
          
          <Table striped bordered hover>
            <thead>
              <tr>
                <th></th>
                <th>SKU
                <span style={{ margin: '0 10px' }}><input
                  type="text"
                  placeholder="Search by SKU"
                  value={skuSearchTerm}
                  onChange={(e) => setSkuSearchTerm(e.target.value)}
                /></span>
                </th>
                <th>BOM Item
                <span style={{ margin: '0 10px' }}><input
                  type="text"
                  placeholder="Search by bom"
                  value={bomItemSearchTerm}
                  onChange={(e) => setBomItemSearchTerm(e.target.value)}
                /></span>
                </th>
                <th>Quantity
                <span style={{ margin: '0 10px' }}><input
                  type="text"
                  placeholder="Search by qty"
                  value={qtySearchTerm}
                  onChange={(e) => setQtySearchTerm(e.target.value)}
                /></span>
                </th>
              </tr>
            </thead>
            <tbody>
  {filteredData.map(bom => (
    <tr key={bom.bomId} onClick={() => handleRowClick(bom)}>
      <td style={{ width: '50px', textAlign: 'center' }}>
                      
      <button
  style={{ display: 'inline-flex', justifyContent: 'center', alignItems: 'center', width: '100%', height: '100%', padding: '0', border: 'none', background: 'none' }}
  className="delete-icon"
  onClick={(e) => {
    e.stopPropagation(); // Stop propagation of the click event
    handleDelete(bom.bomId); // Call handleDelete function
  }}
>
  <DeleteIcon style={{ color: '#F00' }} />
</button>

      </td>
      <td>{bom.skucode}</td>
      <td>{bom.bomItem}</td>
      <td>{bom.qty}</td>
    </tr>
  ))}
</tbody>

          </Table>
        </AccordionDetails>
      </Accordion>
    </div>
  );
}

export default Bom;
