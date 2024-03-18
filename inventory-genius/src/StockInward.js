import { useState, useEffect } from 'react';
import Button from 'react-bootstrap/Button';
import Col from 'react-bootstrap/Col';
import Form from 'react-bootstrap/Form';
import InputGroup from 'react-bootstrap/InputGroup';
import Row from 'react-bootstrap/Row';
import 'bootstrap/dist/css/bootstrap.min.css';
import "./Item.css"
import Header from "./Header"
import { Container } from 'react-bootstrap';
import axios from 'axios';
import Accordion from '@mui/material/Accordion';
import AccordionSummary from '@mui/material/AccordionSummary';
import AccordionDetails from '@mui/material/AccordionDetails';
import ExpandMoreIcon from '@mui/icons-material/ExpandMore';
import * as XLSX from 'xlsx';
import Table from 'react-bootstrap/Table';
import DatePicker from 'react-datepicker';
import 'react-datepicker/dist/react-datepicker.css';
import DeleteIcon from '@mui/icons-material/Delete';

function StockInward() {
  const [validated, setValidated] = useState(false);

  const [date, setDate] = useState("");
  const [skucode, setSkucode] = useState("");
  const [qty, setQty] = useState("");
  const [apiData, setApiData] = useState([]); 
  const [rowSelected, setRowSelected] = useState(false);
  const [selectedItem, setSelectedItem] = useState(null);
  const [skuList, setSkuList] = useState([]);
  const [searchTermSKU, setSearchTermSKU] = useState("");
  const [searchTermDate, setSearchTermDate] = useState("");
  const [searchTermQty, setSearchTermQty] = useState("");

  const filteredData = apiData.filter(supplier => {
    return (
      supplier.date && supplier.date.toString().toLowerCase().includes(searchTermDate.toLowerCase()) && // Add check for existence of date property
      supplier.skucode && supplier.skucode.toString().toLowerCase().includes(searchTermSKU.toLowerCase()) && // Add check for existence of skucode property
      supplier.qty && supplier.qty.toString().toLowerCase().includes(searchTermQty.toLowerCase()) // Add check for existence of qty property
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

        jsonData.shift();

        jsonData.forEach(item => {
            const formattedData = {
                date: item.date,
                skucode: item.skucode,
                qty: item.qty
            };
          console.log(formattedData)
            postData(formattedData);
        });
    };

    reader.readAsBinaryString(file);
};

const handleSubmit = (event) => {
  event.preventDefault();
  const form = event.currentTarget;
  if (form.checkValidity() === false) {
    event.stopPropagation();
  } else {
    // Fetch item based on supplier and supplier SKU code
    axios.get(`http://localhost:8080/items/${skucode}`)
      .then(response => {
        if (response.data) {
          const item = response.data;
          const formData = {
            date,
            skucode,
            qty,
            item: item
          };
          console.log('form data: ', formData);
          axios.post('http://localhost:8080/stockInward', formData)
            .then(response => {
              console.log('POST request successful:', response);
              setValidated(false);
              setApiData([...apiData, response.data]);
              setQty("");
              setDate("");
              setSkucode("");
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

const handleRowSubmit = () => {
  console.log("handleRowSubmit triggered");
  console.log(selectedItem)
  if (rowSelected && selectedItem) {
    const formData = {
      date, 
      skucode,
      qty
    };
    console.log('form data: ', formData)
    console.log("id: ", selectedItem.stockInwardId)
    axios.put(`http://localhost:8080/stockInward/${selectedItem.stockInwardId}`, formData)
      .then(response => {
        
        console.log('PUT request successful:', response);
        setValidated(false);
        setRowSelected(false);
        setDate(""); 
        setSkucode("");
        setQty("")
      })
      .catch(error => {
        console.error('Error sending PUT request:', error);
      });
  }
};



const handleRowClick = (stock) => {
  setDate(stock.date);
  setSkucode(stock.skucode);
  setQty(stock.qty);
  setRowSelected(true);
  setSelectedItem(stock);
};



useEffect(() => {
  axios.get('http://localhost:8080/stockInward') 
    .then(response => setApiData(response.data))
    .catch(error => console.error(error));
    console.log(apiData)
    axios.get('http://localhost:8080/items') // Fetch SKU codes and descriptions from the items table
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
    axios.post('http://localhost:8080/stock', data)
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

  axios.delete(`http://localhost:8080/stockInward/${id}`)
  .then(response => {
    // Handle success response
    console.log('Row deleted successfully.');
    setApiData(prevData => prevData.filter(row => row.stockInwardId !== id));

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
                    <h1>Stock Inward</h1>
            </div>
    <Form noValidate validated={validated} onSubmit={handleSubmit}>
      <Row className="mb-3">
        <Form.Group as={Col} md="4" controlId="validationCustom01">
          <Form.Label>Date</Form.Label>
          <div className="custom-date-picker">
          <DatePicker
            selected={date}
            onChange={date => setDate(date)}
            dateFormat="MM/dd/yyyy"
            placeholderText="Select Date"
            className="form-control" // Apply Bootstrap form control class
          />
        </div>
          <Form.Control.Feedback>Looks good!</Form.Control.Feedback>
        </Form.Group>
        <Form.Group as={Col} md="4" controlId="validationCustom02">
          <Form.Label>SKUcode</Form.Label>
          <Form.Select
                    required
                    onChange={(e) => setSkucode(e.target.value)}
                    value={skucode}
                  >
                    <option value="">Select SKU Code</option>
                    {skuList.map((sku) => (
                      <option key={sku.id} value={sku.skucode}>
                        {sku.skucode} - {sku.description}
                      </option>
                    ))}
                  </Form.Select>
          <Form.Control.Feedback>Looks good!</Form.Control.Feedback>
        </Form.Group>
        
      </Row>

      <Row className="mb-3">
        <Form.Group as={Col} md="4" controlId="validationCustom02">
          <Form.Label>Quantity</Form.Label>
          <Form.Control
            required
            type="text"
            placeholder="Quantity"
            name="Quantity"
            value={qty}
            onChange={(e) => setQty(e.target.value)}
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
      
            <Accordion>
        <AccordionSummary
          expandIcon={<ExpandMoreIcon />}
          aria-controls="panel3-content"
          id="panel3-header"
          sx={{ backgroundColor: '#E5E7E9' }} 
        >
          <h4>List View of Stock</h4>
        </AccordionSummary>
        <AccordionDetails>
        <Table striped bordered hover>
            <thead>
              <tr>
                <th></th>
                <th>Date
                <span style={{ margin: '0 10px' }}><input
                  type="text"
                  placeholder="Search by Date"
                  value={searchTermDate}
                  onChange={(e) => setSearchTermDate(e.target.value)}
                /></span>
                </th>
                  <th>SKUCode
                  <span style={{ margin: '0 10px' }}><input
                  type="text"
                  placeholder="Search by sku"
                  value={searchTermSKU}
                  onChange={(e) => setSearchTermSKU(e.target.value)}
                /></span>
                  </th>
                  
                  <th>Quantity
                  <span style={{ margin: '0 10px' }}><input
                  type="text"
                  placeholder="Search by qty"
                  value={searchTermQty}
                  onChange={(e) => setSearchTermQty(e.target.value)}
                /></span>
                  </th>
              </tr>
            </thead>
            <tbody>
              {filteredData.map(stock => (
                <tr key={stock.stockInwardId} onClick={() => handleRowClick(stock)}>
                  <td style={{ width: '50px', textAlign: 'center' }}>
                      
                  <button
  style={{ display: 'inline-flex', justifyContent: 'center', alignItems: 'center', width: '100%', height: '100%', padding: '0', border: 'none', background: 'none' }}
  className="delete-icon"
  onClick={(e) => {
    e.stopPropagation(); // Stop propagation of the click event
    handleDelete(stock.stockInwardId); // Call handleDelete function
  }}
>
  <DeleteIcon style={{ color: '#F00' }} />
</button>
                    </td>
                  <td>{stock.date}</td>
                  <td>{stock.skucode}</td>
                  <td>{stock.qty}</td>
                </tr>
              ))}
            </tbody>
          </Table>
        </AccordionDetails>
      </Accordion>
                
            </div>
  );
}

export default StockInward;