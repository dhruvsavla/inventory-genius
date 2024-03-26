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

function Stock() {
  const [validated, setValidated] = useState(false);

  const [date, setDate] = useState("");
  const [skucode, setSkucode] = useState("");
  const [addQty, setAddQty] = useState("");
  const [subQty, setSubQty] = useState("");
  const [apiData, setApiData] = useState([]); 
  const [rowSelected, setRowSelected] = useState(false);
  const [selectedItem, setSelectedItem] = useState(null);
  const [skuList, setSkuList] = useState([]);
  const [searchTermSKU, setSearchTermSKU] = useState("");
  const [searchTermDate, setSearchTermDate] = useState("");
  const [searchTermAdd, setSearchTermAdd] = useState("");
  const [searchTermSub, setSearchTermSub] = useState("");

  useEffect(() => {
    const currentDate = new Date(); // Get current date
    const year = currentDate.getFullYear(); // Get current year
    const month = String(currentDate.getMonth() + 1).padStart(2, '0'); // Get current month and pad with leading zero if needed
    const day = String(currentDate.getDate() + 1).padStart(2, '0'); // Get current day and pad with leading zero if needed
    const formattedDate = `${year}-${month}-${day}`; // Format date as YYYY-MM-DD
    setDate(formattedDate); // Set the default date state
    
  }, []);

  const filteredData = apiData.filter(supplier => {
    return (
      supplier.date.toString().toLowerCase().includes(searchTermDate.toLowerCase()) && // Convert to string
      supplier.skucode.toString().toLowerCase().includes(searchTermSKU.toLowerCase()) &&
      supplier.addQty.toString().toLowerCase().includes(searchTermAdd.toLowerCase()) &&
      supplier.subQty.toString().toLowerCase().includes(searchTermSub.toLowerCase())
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
                addQty: item.addQty,
                subQty: item.subQty
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
    axios.get(`http://localhost:8080/item/supplier/search/skucode/${skucode}`)
      .then(response => {
        if (response.data) {
          const item = response.data;
          const formData = {
            date,
            skucode,
            addQty,
            subQty,
            item: item
          };
          console.log('form data: ', formData);
          axios.post('http://localhost:8080/stock', formData)
            .then(response => {
              console.log('POST request successful:', response);
              setValidated(false);
              setApiData([...apiData, response.data]);
              setAddQty("");
              setSubQty("");
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
      addQty,
      subQty
    };
    console.log('form data: ', formData)
    console.log("id: ", selectedItem.stockId)
    axios.put(`http://localhost:8080/stock/${selectedItem.stockId}`, formData)
      .then(response => {
        
        console.log('PUT request successful:', response);
        setApiData(prevData => prevData.map(item => item.stockId === selectedItem.stockId ? response.data : item)); // Update the specific item

        setValidated(false);
        setRowSelected(false);
        setAddQty(""); 
        setSkucode("");
        setAddQty("");
        setSubQty("")
      })
      .catch(error => {
        console.error('Error sending PUT request:', error);
      });
  }
};



const handleRowClick = (stock) => {
  setDate(stock.date);
  setSkucode(stock.skucode);
  setAddQty(stock.addQty);
  setSubQty(stock.subQty);
  setRowSelected(true);
  setSelectedItem(stock);
};



useEffect(() => {
  axios.get('http://localhost:8080/stock') 
    .then(response => setApiData(response.data))
    .catch(error => console.error(error));
    console.log(apiData)
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

  axios.delete(`http://localhost:8080/stock/${id}`)
  .then(response => {
    // Handle success response
    console.log('Row deleted successfully.');
    setApiData(prevData => prevData.filter(row => row.stockId !== id));

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
                    <h1>Stock</h1>
            </div>
            <Accordion defaultExpanded>
        <AccordionSummary className='acc-summary'
          expandIcon={<ExpandMoreIcon />}
          aria-controls="panel3-content"
          id="panel3-header"
          sx={{ backgroundColor: '#E5E7E9' }} 
        >
          <h4>Stock Form</h4>
        </AccordionSummary>
        <AccordionDetails>
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
        <Form.Group as={Col} md="4" controlId="validationCustom01">
          <Form.Label>AddQty</Form.Label>
          <Form.Control
             required
             type="text"
             placeholder="AddQty"
             name="AddQty"
             value={addQty}
             onChange={(e) => setAddQty(e.target.value)}
          />
          <Form.Control.Feedback>Looks good!</Form.Control.Feedback>
        </Form.Group>
        <Form.Group as={Col} md="4" controlId="validationCustom02">
          <Form.Label>SubQty</Form.Label>
          <Form.Control
            required
            type="text"
            placeholder="SubQty"
            name="SubQty"
            value={subQty}
            onChange={(e) => setSubQty(e.target.value)}
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
                  <th>AddQty
                  <span style={{ margin: '0 10px' }}><input
                  type="text"
                  placeholder="Search by addqty"
                  value={searchTermAdd}
                  onChange={(e) => setSearchTermAdd(e.target.value)}
                /></span>
                  </th>
                  <th>SubQty
                  <span style={{ margin: '0 10px' }}><input
                  type="text"
                  placeholder="Search by subqty"
                  value={searchTermSub}
                  onChange={(e) => setSearchTermSub(e.target.value)}
                /></span>
                  </th>
              </tr>
            </thead>
            <tbody>
              {filteredData.map(stock => (
                <tr key={stock.stockId} onClick={() => handleRowClick(stock)}>
                  <td style={{ width: '50px', textAlign: 'center' }}>
                      
                  <button
  style={{ display: 'inline-flex', justifyContent: 'center', alignItems: 'center', width: '100%', height: '100%', padding: '0', border: 'none', background: 'none' }}
  className="delete-icon"
  onClick={(e) => {
    e.stopPropagation(); // Stop propagation of the click event
    handleDelete(stock.stockId); // Call handleDelete function
  }}
>
  <DeleteIcon style={{ color: '#F00' }} />
</button>

                    </td>
                  <td>{stock.date}</td>
                  <td>{stock.skucode}</td>
                  <td>{stock.addQty}</td>
                  <td>{stock.subQty}</td>
                </tr>
              ))}
            </tbody>
          </Table>
        </AccordionDetails>
      </Accordion>
                
            </div>
  );
}

export default Stock;