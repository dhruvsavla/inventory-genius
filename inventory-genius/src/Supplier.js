// Import useState hook from React
import React, { useState, useEffect } from 'react';
import Button from 'react-bootstrap/Button';
import Col from 'react-bootstrap/Col';
import Form from 'react-bootstrap/Form';
import InputGroup from 'react-bootstrap/InputGroup';
import Row from 'react-bootstrap/Row';
import 'bootstrap/dist/css/bootstrap.min.css';
import Table from 'react-bootstrap/Table';
import "./Item.css"
import Header from "./Header"
import * as XLSX from 'xlsx';
import axios from 'axios';
import Accordion from '@mui/material/Accordion';
import AccordionSummary from '@mui/material/AccordionSummary';
import AccordionDetails from '@mui/material/AccordionDetails';
import ExpandMoreIcon from '@mui/icons-material/ExpandMore';
import DeleteIcon from '@mui/icons-material/Delete';

function Supplier() {
  const [validated, setValidated] = useState(false);
  const [phonel, setPhone] = useState("");
  const [address, setAddress] = useState("");
  const [supplierName, setName] = useState("");
  const [apiData, setApiData] = useState([]); 
  const [rowSelected, setRowSelected] = useState(false);
  const [selectedItem, setSelectedItem] = useState(null);
  const [searchTermName, setSearchTermName] = useState('');
  const [searchTermAddress, setSearchTermAddress] = useState('');
  const [searchTermPhone, setSearchTermPhone] = useState('');

  // Filter the apiData based on the searchTerm
  const filteredData = apiData.filter(supplier => {
    return (
      supplier.supplierName.toLowerCase().includes(searchTermName.toLowerCase()) &&
      supplier.address.toLowerCase().includes(searchTermAddress.toLowerCase()) &&
      supplier.phonel.toLowerCase().includes(searchTermPhone.toLowerCase())
    );
  });

  const handleFileUpload = (e) => {
    const file = e.target.files[0];
    const reader = new FileReader();

    reader.onload = (evt) => {
        const data = evt.target.result;
        const workbook = XLSX.read(data, { type: 'binary' });
        const sheetName = workbook.SheetNames[0];
        const sheet = workbook.Sheets[sheetName];
        const jsonData = XLSX.utils.sheet_to_json(sheet);

        jsonData.forEach(item => {
            const formattedData = {
                address: item.address,
                phonel: item.phone,
                supplierName: item.supplier_name
            };
            console.log(formattedData);
            postData(formattedData);
        });
    };

    reader.readAsBinaryString(file);
};


const handleSubmit = (event) => {
  event.preventDefault();
  const form = event.currentTarget;

  if (form.checkValidity() === false || !phonel || !supplierName) {
    event.stopPropagation();
    setValidated(true); 
    return;
  }

  if (form.checkValidity() === false) {
    event.stopPropagation();
  } else {
    const formData = {
      phonel,
      address,
      supplierName,
    };

    axios
      .post('http://localhost:8080/supplier', formData)
      .then((response) => {
        console.log('POST request successful:', response);
        setValidated(false);
        setApiData([...apiData, response.data]);
        setPhone(""); 
        setAddress(""); 
        setName(""); 
      })
      .catch((error) => {
        console.error('Error sending POST request:', error);
      });
  }
};

const handleRowSubmit = () => {
  console.log("handleRowSubmit triggered");
  console.log(selectedItem)
  if (rowSelected && selectedItem) {
    const formData = {
      phonel,
      address,
      supplierName
    };
    console.log('form data: ', formData)
    console.log("id: ", selectedItem.supplierId)
    axios.put(`http://localhost:8080/supplier/${selectedItem.supplierId}`, formData)
      .then(response => {
        
        console.log('PUT request successful:', response);
        setApiData(prevData => prevData.map(item => item.supplierId === selectedItem.supplierId ? response.data : item)); // Update the specific item

        setValidated(false);
        setRowSelected(false);
        setPhone("");
        setAddress("");
        setName("");
      })
      .catch(error => {
        console.error('Error sending PUT request:', error);
      });
  }
};

const handleRowClick = (supplier) => {
  setAddress(supplier.address);
  setPhone(supplier.phonel);
  setName(supplier.supplierName);
  setRowSelected(true);
  setSelectedItem(supplier);
};

useEffect(() => {
  axios.get('http://localhost:8080/supplier') 
    .then(response => setApiData(response.data))
    .catch(error => console.error(error));
}, []);

const postData = (data) => {
    axios.post('http://localhost:8080/supplier', data)
        .then(response => {
            // Handle successful response
            console.log('Data posted successfully:', response);
        })
        .catch(error => {
            // Handle error
            console.error('Error posting data:', error);
        });
};

const handleDelete = (id) => {
  console.log("Deleting row with id:", id);
  // Remove the row from the table

  axios.delete(`http://localhost:8080/supplier/${id}`)
  .then(response => {
    // Handle success response
    console.log('Row deleted successfully.');
    setApiData(prevData => prevData.filter(row => row.supplierId !== id));

  })
  .catch(error => {
    // Handle error
    console.error('Error deleting row:', error);
  });

  console.log("After deletion, apiData:", apiData);
};

return (
  <div>
    <div className='title'>
      <h1>Supplier</h1>
    </div>
  
    <Accordion defaultExpanded>
    <AccordionSummary className='acc-summary'
      expandIcon={<ExpandMoreIcon />}
      aria-controls="panel3-content"
      id="panel3-header"
      sx={{ backgroundColor: '#E5E7E9' }} 
    >
      <h4>Supplier Form</h4>
    </AccordionSummary>
    <AccordionDetails>
    <Form noValidate validated={validated} onSubmit={handleSubmit}>
      <Row className="mb-3">
        <Form.Group as={Col} md="4" controlId="validationCustom01">
          <Form.Label>Name</Form.Label>
          <Form.Control
              required
              type="text"
              placeholder="Supplier Name"
              name="supplierName"
              value={supplierName}
              onChange={(e) => setName(e.target.value)}
            />
      <Form.Control.Feedback>Looks good!</Form.Control.Feedback>
    </Form.Group>
    <Form.Group as={Col} md="4" controlId="validationCustom02">
      <Form.Label>Address</Form.Label>
      <Form.Control
              required
              type="text"
              placeholder="Address"
              name="address"
              value={address}
              onChange={(e) => setAddress(e.target.value)}
            />
      <Form.Control.Feedback>Looks good!</Form.Control.Feedback>
    </Form.Group>
    <Form.Group as={Col} md="4" controlId="validationCustom01">
      <Form.Label>Phone</Form.Label>
      <Form.Control
              required
              type="text"
              placeholder="Phone"
              name="phonel"
              value={phonel}
              onChange={(e) => setPhone(e.target.value)}
            />
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
        <h4>List View of Suppliers</h4>
      </AccordionSummary>
      <AccordionDetails>
      <div style={{ overflowX: 'auto' }}> 
      <Table striped bordered hover>
          <thead>
            <tr>
              <th></th>
              <th>Name
              <span style={{ margin: '0 10px' }}><input
                type="text"
                placeholder="Search by name"
                value={searchTermName}
                onChange={(e) => setSearchTermName(e.target.value)}
              /></span>
              </th>
              <th>Address
              <span style={{ margin: '0 10px' }}><input
                type="text"
                placeholder="Search by address"
                value={searchTermAddress}
                onChange={(e) => setSearchTermAddress(e.target.value)}
              />
              </span>
              </th>
              <th>Phone No
              <span style={{ margin: '0 10px' }}><input
                type="text"
                placeholder="Search by phone"
                value={searchTermPhone}
                onChange={(e) => setSearchTermPhone(e.target.value)}
              />
              </span>
              </th>
            </tr>
          </thead>
          <tbody>
            {filteredData.map(supplier => (
              <tr key={supplier.supplierId} onClick={() => handleRowClick(supplier)}>
                  <td style={{ width: '50px', textAlign: 'center' }}>
                    
                  <button
style={{ display: 'inline-flex', justifyContent: 'center', alignItems: 'center', width: '100%', height: '100%', padding: '0', border: 'none', background: 'none' }}
className="delete-icon"
onClick={(e) => {
  e.stopPropagation(); // Stop propagation of the click event
  handleDelete(supplier.supplierId); // Call handleDelete function
}}
>
<DeleteIcon style={{ color: '#F00' }} />
</button>

                </td>

                <td>{supplier.supplierName}</td>
                <td>{supplier.address}</td>
                <td>{supplier.phonel}</td>
              </tr>
            ))}
          </tbody>
        
        </Table>
        </div>
      </AccordionDetails>
    </Accordion>
    {/* Display the image based on the entered URL */}
    
        </div>
);
}

export default Supplier;
