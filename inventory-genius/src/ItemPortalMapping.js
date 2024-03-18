import { useState, useEffect } from 'react';
import Button from 'react-bootstrap/Button';
import Col from 'react-bootstrap/Col';
import Form from 'react-bootstrap/Form';
import InputGroup from 'react-bootstrap/InputGroup';
import Row from 'react-bootstrap/Row';
import 'bootstrap/dist/css/bootstrap.min.css';
import "./Item.css"
import { Table } from 'react-bootstrap';
import Header from "./Header"
import axios from 'axios';
import Accordion from '@mui/material/Accordion';
import AccordionSummary from '@mui/material/AccordionSummary';
import AccordionDetails from '@mui/material/AccordionDetails';
import ExpandMoreIcon from '@mui/icons-material/ExpandMore';
import * as XLSX from 'xlsx';
import DeleteIcon from '@mui/icons-material/Delete';

function ItemPortalMapping() {
    const [validated, setValidated] = useState(false);
    const [portal, setPortal] = useState("");
    const [supplier, setSeller] = useState("");
    const [sellerSkuCode, setSellerSKU] = useState("");
    const [portalSkuCode, setPortalSKU] = useState("");
    const [apiData, setApiData] = useState([]); 
    const [rowSelected, setRowSelected] = useState(false);
    const [selectedItem, setSelectedItem] = useState(null);
    const [searchTermPortal, setSearchTermPortal] = useState('');
    const [searchTermSupplier, setSearchTermSupplier] = useState('');
    const [searchTermSellerSKU, setSearchTermSellerSKU] = useState('');
    const [searchTermPortalSKU, setSearchTermPortalSKU] = useState('');

    const filteredData = apiData.filter(supplier => {
      return (
        supplier.portal.toLowerCase().includes(searchTermPortal.toLowerCase()) &&
        supplier.supplier.toLowerCase().includes(searchTermSupplier.toLowerCase()) &&
        supplier.portalSkuCode.toLowerCase().includes(searchTermPortalSKU.toLowerCase()) &&
        supplier.sellerSkuCode.toLowerCase().includes(searchTermSellerSKU.toLowerCase())
      );
    });

    const handleSubmit = (event) => {
      event.preventDefault();
      const form = event.currentTarget;
      if (form.checkValidity() === false) {
        event.stopPropagation();
      } else {
        // Fetch item based on supplier and supplier SKU code
        axios.get(`http://localhost:8080/items/${sellerSkuCode}/${supplier}`)
          .then(response => {
            if (response.data) {
              const item = response.data;
              const formData = {
                portal,
                supplier,
                portalSkuCode,
                sellerSkuCode,
                item: item
              };
              console.log('form data: ', formData);
              axios.post('http://localhost:8080/itemportalmapping', formData)
                .then(response => {
                  console.log('POST request successful:', response);
                  setValidated(false);
                  setApiData([...apiData, response.data]);
                  setPortal(""); 
                  setPortalSKU(""); 
                  setSellerSKU("");
                  setSeller(""); 
                })
                .catch(error => {
                  console.error('Error sending POST request:', error);
                });
            } else {
              console.error('No item found for the specified supplier and supplier SKU code.');
            }
          })
          .catch(error => {
            console.error('Error fetching item:', error);
          });
      }
    
      setValidated(true);
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
  
          jsonData.shift();
  
          jsonData.forEach(item => {
              const formattedData = {
                  portal: item.portal,
                  supplier: item.supplier,
                  sellerSkuCode: item.sellerSkuCode,
                  portalSkuCode: item.portalSkuCode
              };
            console.log(formattedData)
              postData(formattedData);
          });
      };
  
      reader.readAsBinaryString(file);
  };
  
  const handleDelete = (id) => {
    console.log("Deleting row with id:", id);
    // Remove the row from the table
  
    axios.delete(`http://localhost:8080/itemportalmapping/${id}`)
    .then(response => {
      // Handle success response
      console.log('Row deleted successfully.');
      setApiData(prevData => prevData.filter(row => row.id !== id));

    })
    .catch(error => {
      // Handle error
      console.error('Error deleting row:', error);
    });

    console.log("After deletion, apiData:", apiData);
  };
    
  
    const handleRowSubmit = () => {
      console.log("handleRowSubmit triggered");
      console.log(selectedItem)
      if (rowSelected && selectedItem) {
        const formData = {
          portal,
          supplier,
          portalSkuCode,
          sellerSkuCode
        };
        console.log('form data: ', formData)
        console.log("id: ", selectedItem.id)
        axios.put(`http://localhost:8080/itemportalmapping/${selectedItem.id}`, formData)
          .then(response => {
            
            console.log('PUT request successful:', response);
            setValidated(false);
            setRowSelected(false);
            setPortal("");
            setSeller("");
            setPortalSKU("");
            setSellerSKU("");
          })
          .catch(error => {
            console.error('Error sending PUT request:', error);
          });
      }
    };
  
    const handleRowClick = (ipm) => {
      setPortal(ipm.portal);
      setSeller(ipm.supplier);
      setPortalSKU(ipm.portalSkuCode);
      setSellerSKU(ipm.sellerSkuCode);
      setRowSelected(true);
      setSelectedItem(ipm);
    };
    
    
    useEffect(() => {
      axios.get('http://localhost:8080/itemportalmapping') 
        .then(response => setApiData(response.data))
        .catch(error => console.error(error));
        console.log(apiData)
    }, []);
    
  const postData = (data) => {
    axios.post('http://localhost:8080/itemportalmapping', data)
      .then(response => {
        // Handle successful response
        console.log('Data posted successfully:', response);
      })
      .catch(error => {
        // Handle error
        console.error('Error posting data:', error);
      });
  };    

    return (
        <div>
            <div className='title'>
                    <h1>Import Portal Mapping</h1>
                </div>
        <Accordion defaultExpanded>
        <AccordionSummary className='acc-summary'
          expandIcon={<ExpandMoreIcon />}
          aria-controls="panel3-content"
          id="panel3-header"
          sx={{ backgroundColor: '#E5E7E9' }} 
        >
          <h4>ItemPortalMapping Form</h4>
        </AccordionSummary>
        <AccordionDetails>
    <Form noValidate validated={validated} onSubmit={handleSubmit}>
      <Row className="mb-3">
        <Form.Group as={Col} md="4" controlId="validationCustom01">
          <Form.Label>Portal</Form.Label>
          <Form.Control
            required
            type="text"
            placeholder="Portal"
            defaultValue=""
            value={portal }
            onChange={(e) => setPortal(e.target.value)}
          />
          <Form.Control.Feedback>Looks good!</Form.Control.Feedback>
        </Form.Group>
        <Form.Group as={Col} md="4" controlId="validationCustom02">
          <Form.Label>Seller/Supplier</Form.Label>
          <Form.Control
            required
            type="text"
            placeholder="Seller/Supplier"
            defaultValue=""
            value={supplier }
            onChange={(e) => setSeller(e.target.value)}
          />
          <Form.Control.Feedback>Looks good!</Form.Control.Feedback>
        </Form.Group>
        
      </Row>
      <Row className="mb-3">
        <Form.Group as={Col} md="4" controlId="validationCustom01">
          <Form.Label>Portal SKUcode</Form.Label>
          <Form.Control
            required
            type="text"
            placeholder="Portal SKUcode"
            defaultValue=""
            value={ portalSkuCode}
            onChange={(e) => setPortalSKU(e.target.value)}
          />
          <Form.Control.Feedback>Looks good!</Form.Control.Feedback>
        </Form.Group>
        <Form.Group as={Col} md="4" controlId="validationCustom02">
          <Form.Label>Seller/Supplier SKUcode</Form.Label>
          <Form.Control
            required
            type="text"
            placeholder="Seller/Supplier SKUcode"
            defaultValue=""
            value={sellerSkuCode }
            onChange={(e) => setSellerSKU(e.target.value)}
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
          <h4>List View of Bom</h4>
        </AccordionSummary>
        <AccordionDetails>
        <Table striped bordered hover>
            <thead>
              <tr>
                <th></th>
                <th>Portal
                <span style={{ margin: '0 10px' }}><input
                  type="text"
                  placeholder="Search by Portal"
                  value={searchTermPortal}
                  onChange={(e) => setSearchTermPortal(e.target.value)}
                /></span>
                </th>
                <th>Portal SKUCode
                <span style={{ margin: '0 10px' }}><input
                  type="text"
                  placeholder="Search by PortalSKU"
                  value={searchTermPortalSKU}
                  onChange={(e) => setSearchTermPortalSKU(e.target.value)}
                /></span>
                </th>
                  <th>Supplier
                  <span style={{ margin: '0 10px' }}><input
                  type="text"
                  placeholder="Search by Supplier"
                  value={searchTermSupplier}
                  onChange={(e) => setSearchTermSupplier(e.target.value)}
                /></span>
                  </th>
                  <th>Supplier SKUCode
                  <span style={{ margin: '0 10px' }}><input
                  type="text"
                  placeholder="Search by SupplierSKU"
                  value={searchTermSellerSKU}
                  onChange={(e) => setSearchTermSellerSKU(e.target.value)}
                /></span>
                  </th>
              </tr>
            </thead>
            <tbody>
              {filteredData.map(ipm => (
                <tr key={ipm.id} onClick={() => handleRowClick(ipm)}>
                  <td style={{ width: '50px', textAlign: 'center' }}>
                      
                  <button
  style={{ display: 'inline-flex', justifyContent: 'center', alignItems: 'center', width: '100%', height: '100%', padding: '0', border: 'none', background: 'none' }}
  className="delete-icon"
  onClick={(e) => {
    e.stopPropagation(); // Stop propagation of the click event
    handleDelete(ipm.id); // Call handleDelete function
  }}
>
  <DeleteIcon style={{ color: '#F00' }} />
</button>

                    </td>
                  <td>{ipm.portal}</td>
                  <td>{ipm.portalSkuCode}</td>
                  <td>{ipm.supplier}</td>
                  <td>{ipm.sellerSkuCode}</td>
                </tr>
              ))}
            </tbody>
          </Table>
        </AccordionDetails>
      </Accordion>
            </div>
  );
}

export default ItemPortalMapping;