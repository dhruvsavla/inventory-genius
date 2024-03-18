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
import * as XLSX from 'xlsx';
import axios from 'axios';
import Accordion from '@mui/material/Accordion';
import AccordionSummary from '@mui/material/AccordionSummary';
import AccordionDetails from '@mui/material/AccordionDetails';
import ExpandMoreIcon from '@mui/icons-material/ExpandMore';
import DatePicker from 'react-datepicker';
import 'react-datepicker/dist/react-datepicker.css';
import DeleteIcon from '@mui/icons-material/Delete';

function ImportOrderForm() {
  const [validated, setValidated] = useState(false);
  const [date, setDate] = useState("");
  const [orderNo, setOrderno] = useState("");
  const [portalOrderNo, setPortalOrderno] = useState("");
  const [portalOrderLineId, setPortalOrderLineid] = useState("");
  const [portalSKU, setPortalSKU] = useState("");
  const [productDescription, setProductDescription] = useState("");
  const [shipByDate, setShipbyDate] = useState("");
  const [dispatched, setDispatched] = useState("");
  const [courier, setCourier] = useState("");
  const [portal, setPortal] = useState("");
  const [sellerSKU, setSellerSKU] = useState("");
  const [qty, setQuantity] = useState("");
  const [apiData, setApiData] = useState([]); 
  const [rowSelected, setRowSelected] = useState(false);
  const [selectedItem, setSelectedItem] = useState(null);
  const [portalSKUList, setPortalSKUList] = useState([]);
  const [sellerSKUList, setSellerSKUList] = useState([]);
  const [itemDescriptionList, setItemDescriptionList] = useState([]);
  const [portalNameList, setPortalNameList] = useState([]);

  const [searchTermDate, setSearchTermDate] = useState('');
  const [searchTermOrderNo, setSearchTermOrderNo] = useState('');
  const [searchTermPortalOrderNo, setSearchTermPortalOrderNo] = useState('');
  const [searchTermPortalLineId, setSearchTermPortalLineId] = useState('');
  const [searchTermQuantity, setSearchTermQuantity] = useState('');
  const [searchTermCourier, setSearchTermCourier] = useState('');
  const [searchTermDispatched, setSearchTermDispatched] = useState('');
  const [searchTermSellerSKU, setSearchTermSellerSKU] = useState('');
  const [searchTermPortalSKU, setSearchTermPortalSKU] = useState('');
  const [searchTermShibByDate, setSearchTermShibByDate] = useState('');
  const [searchTermProductDescription, setSearchTermProductDescription] = useState('');
  const [searchTermPortal, setSearchTermPortal] = useState('');

  const formatDate = (date) => {
    return date ? new Date(date).toLocaleDateString().toLowerCase() : '';
  };

  console.log("apiData:", apiData);
  const filteredData = apiData.filter(item =>
    formatDate(item.date).includes(searchTermDate.toLowerCase()) &&
    formatDate(item.shipByDate).includes(searchTermShibByDate.toLowerCase()) &&
    item.orderNo && item.orderNo.toString().toLowerCase().includes(searchTermOrderNo.toLowerCase()) &&
    item.portalOrderNo && item.portalOrderNo.toString().toLowerCase().includes(searchTermPortalOrderNo.toLowerCase()) &&
    item.portalOrderLineId && item.portalOrderLineId.toString().toLowerCase().includes(searchTermPortalLineId.toLowerCase()) &&
    item.qty && item.qty.toString().toLowerCase().includes(searchTermQuantity.toLowerCase()) &&
    item.courier && item.courier.toString().toLowerCase().includes(searchTermCourier.toLowerCase()) &&
    item.dispatched && item.dispatched.toString().toLowerCase().includes(searchTermDispatched.toLowerCase()) &&
       item.sellerSKU && item.sellerSKU.toString().toLowerCase().includes(searchTermSellerSKU.toLowerCase()) &&
       item.portalSKU && item.portalSKU.toString().toLowerCase().includes(searchTermPortalSKU.toLowerCase()) &&
       item.productDescription && item.productDescription.toString().toLowerCase().includes(searchTermProductDescription.toLowerCase()) &&
       item.portal && item.portal.toString().toLowerCase().includes(searchTermPortal.toLowerCase())

    // Add other filtering conditions here...
  );
  console.log("filteredData:", filteredData);

  // const filteredData = apiData.filter(item => {
  //   return (
  //     formatDate(item.date).includes(searchTermDate.toLowerCase()) &&
  //     formatDate(item.shipByDate).includes(searchTermShibByDate.toLowerCase()) &&
  //     item.orderNo && item.orderNo.toString().toLowerCase().includes(searchTermOrderNo.toLowerCase()) &&
  //     item.portalOrderNo && item.portalOrderNo.toString().toLowerCase().includes(searchTermPortalOrderNo.toLowerCase()) &&
  //     item.portalOrderLineId && item.portalOrderLineId.toString().toLowerCase().includes(searchTermPortalLineId.toLowerCase()) &&
  //     item.quantity && item.quantity.toString().toLowerCase().includes(searchTermQuantity.toLowerCase()) &&
  //     item.courier && item.courier.toString().toLowerCase().includes(searchTermCourier.toLowerCase()) &&
  //     item.dispatched && item.dispatched.toString().toLowerCase().includes(searchTermDispatched.toLowerCase()) &&
  //     item.sellerSKU && item.sellerSKU.toString().toLowerCase().includes(searchTermSellerSKU.toLowerCase()) &&
  //     item.portalSKU && item.portalSKU.toString().toLowerCase().includes(searchTermPortalSKU.toLowerCase()) &&
  //     item.productDescription && item.productDescription.toString().toLowerCase().includes(searchTermProductDescription.toLowerCase()) &&
  //     item.portal && item.portal.toString().toLowerCase().includes(searchTermPortal.toLowerCase())
  //   );
    
  // });
  
  
  

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
                // bin: item.binNumber,
                // rack: item.rackNumber,
                // skucde: item.skucode
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
      axios.get(`http://localhost:8080/items/order/${sellerSKU}/${productDescription}`)
        .then(response => {
          if (response.data) {
            const itemsArray = []; // Initialize an array to store items
            itemsArray.push(response.data);
            const formData = {
              date,
              orderNo,
              portalOrderNo,
              portalOrderLineId,
              portalSKU,
              productDescription,
              shipByDate,
              dispatched,
              courier,
              portal,
              sellerSKU,
              qty,
              items: itemsArray
            };
            console.log('form data: ', formData);
            axios.post('http://localhost:8080/orders', formData)
              .then(response => {
                console.log('POST request successful:', response);
                setValidated(false);
                setApiData([...apiData, response.data]);
             
                setDate("");
                setCourier("");
                setDispatched("")
                setOrderno("");
                setPortal("");
                setPortalOrderno("")
                setPortalOrderLineid("");
                setQuantity("");
                setShipbyDate("")
                setProductDescription("");
                setSellerSKU("");
                setPortalSKU("")
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
      orderNo,
      portalOrderNo,
      portalOrderLineId,
      portalSKU,
      productDescription,
      shipByDate,
      dispatched,
      courier,
      portal,
      sellerSKU,
      qty,
    };
    console.log('form data: ', formData)
    console.log("id: ", selectedItem.orderId)
    axios.put(`http://localhost:8080/orders/${selectedItem.orderId}`, formData)
      .then(response => {
        
        console.log('PUT request successful:', response);
        setValidated(false);
        setRowSelected(false);
        setDate("");
        setOrderno("");
        setPortalOrderno("");
        setPortalOrderLineid("");
        setPortal("");
        setPortalSKU("");
        setProductDescription("");
        setCourier("");
        setDispatched("");
        setQuantity("");
        setSellerSKU("");
        setShipbyDate("");
      })
      .catch(error => {
        console.error('Error sending PUT request:', error);
      });
  }
};



const handleRowClick = (order) => {
  setDate(order.date);
        setOrderno(order.orderNo);
        setPortalOrderno(order.portalOrderNo);
        setPortalOrderLineid(order.portalOrderLineId);
        setPortal(order.portal);
        setPortalSKU(order.portalSKU);
        setProductDescription(order.productDescription);
        setCourier(order.courier);
        setDispatched(order.dispatched);
        setQuantity(order.qty);
        setSellerSKU(order.sellerSKU);
        setShipbyDate(order.shipByDate);
  setRowSelected(true);
  setSelectedItem(order);
};


useEffect(() => {
  axios.get('http://localhost:8080/orders') 
    .then(response => setApiData(response.data))
      .catch(error => console.error(error));
    console.log(apiData)
    axios.get('http://localhost:8080/itemportalmapping')
    .then(response => {
      // Extract portal SKUs from the response data
      const portalSKUs = response.data.map(item => item.portalSkuCode);
      const sellerSKUs = response.data.map(seller => seller.sellerSkuCode);
      const portalName = response.data.map(portal => portal.portal);
      setSellerSKUList(sellerSKUs);
      setPortalSKUList(portalSKUs);
      setPortalNameList(portalName);
    })
    .catch(error => {
      console.error('Error fetching portal SKUs:', error);
    });
    axios.get('http://localhost:8080/items')
    .then(response => {
      // Extract portal SKUs from the response data
      const itemDescription = response.data.map(item => item.description);
      setItemDescriptionList(itemDescription);
      console.log("portal list = " + portalSKUList)
    })
    .catch(error => {
      console.error('Error fetching portal SKUs:', error);
    });
}, []);

const postData = (data) => {
    axios.post('http://localhost:8080/orders', data)
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

  axios.delete(`http://localhost:8080/orders/${id}`)
  .then(response => {
    // Handle success response
    console.log('Row deleted successfully.');
    setApiData(prevData => prevData.filter(row => row.orderId !== id));

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
                    <h1>Import Order Form</h1>
                </div>
                <Accordion defaultExpanded>
        <AccordionSummary className='acc-summary'
          expandIcon={<ExpandMoreIcon />}
          aria-controls="panel3-content"
          id="panel3-header"
          sx={{ backgroundColor: '#E5E7E9' }} 
        >
          <h4>Storage Form</h4>
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
        
        <Form.Group as={Col} md="4" controlId="validationCustom01">
          <Form.Label>Order No</Form.Label>
          <Form.Control
            required
            type="text"
            placeholder="Order No"
                  defaultValue=""
                  value={orderNo}
                onChange={(e) => setOrderno(e.target.value)}
          />
          <Form.Control.Feedback>Looks good!</Form.Control.Feedback>
        </Form.Group>

        <Form.Group as={Col} md="4" controlId="validationCustom01">
          <Form.Label>Portal</Form.Label>
          <Form.Select
          required
          value={portal} // Set the selected value
          onChange={(e) => setPortal(e.target.value)} // Handle value change
        >
          <option value="">Select Portal </option>
          {/* Map over portalSKUList and create options */}
          {portalNameList.map((sku, index) => (
            <option key={index} value={sku}>{sku}</option>
          ))}
        </Form.Select>
          <Form.Control.Feedback>Looks good!</Form.Control.Feedback>
        </Form.Group>
        
      </Row>

      <Row className="mb-3">
        <Form.Group as={Col} md="4" controlId="validationCustom01">
          <Form.Label>Portal OrderNo</Form.Label>
          <Form.Control
            required
            type="text"
            placeholder="Portal OrderNo"
                  defaultValue=""
                  value={portalOrderNo}
                onChange={(e) => setPortalOrderno(e.target.value)}
          />
          <Form.Control.Feedback>Looks good!</Form.Control.Feedback>
        </Form.Group>
        <Form.Group as={Col} md="4" controlId="validationCustom02">
          <Form.Label>Portal OrderLineid</Form.Label>
          <Form.Control
            required
            type="text"
            placeholder="Portal OrderLineid"
                  defaultValue=""
                  value={portalOrderLineId}
                  onChange={(e) => setPortalOrderLineid(e.target.value)}
          />
          <Form.Control.Feedback>Looks good!</Form.Control.Feedback>
        </Form.Group>
        
        <Form.Group as={Col} md="4" controlId="validationCustom01">
          <Form.Label>Portal SKU</Form.Label>
          <Form.Select
          required
          value={portalSKU} // Set the selected value
          onChange={(e) => setPortalSKU(e.target.value)} // Handle value change
        >
          <option value="">Select Portal SKU</option>
          {/* Map over portalSKUList and create options */}
          {portalSKUList.map((sku, index) => (
            <option key={index} value={sku}>{sku}</option>
          ))}
        </Form.Select>
          <Form.Control.Feedback>Looks good!</Form.Control.Feedback>
        </Form.Group>
        
        </Row>

         <Row className="mb-3">
        <Form.Group as={Col} md="4" controlId="validationCustom01">
          <Form.Label>Seller SKU</Form.Label>
          <Form.Select
          required
          value={sellerSKU} // Set the selected value
          onChange={(e) => setSellerSKU(e.target.value)} // Handle value change
        >
          <option value="">Select Seller SKU</option>
          {/* Map over portalSKUList and create options */}
          {sellerSKUList.map((sku, index) => (
            <option key={index} value={sku}>{sku}</option>
          ))}
        </Form.Select>
          <Form.Control.Feedback>Looks good!</Form.Control.Feedback>
        </Form.Group>
        <Form.Group as={Col} md="4" controlId="validationCustom02">
          <Form.Label>Product Description</Form.Label>
          <Form.Select
          required
          value={productDescription} // Set the selected value
          onChange={(e) => setProductDescription(e.target.value)} // Handle value change
        >
          <option value="">Select Seller SKU</option>
          {/* Map over portalSKUList and create options */}
          {itemDescriptionList.map((sku, index) => (
            <option key={index} value={sku}>{sku}</option>
          ))}
        </Form.Select>
          <Form.Control.Feedback>Looks good!</Form.Control.Feedback>
        </Form.Group>
        
        <Form.Group as={Col} md="4" controlId="validationCustom01">
          <Form.Label>Quantity</Form.Label>
          <Form.Control
            required
            type="text"
            placeholder="Quantity"
                  defaultValue=""
                  value={qty}
                  onChange={(e) => setQuantity(e.target.value)}
          />
          <Form.Control.Feedback>Looks good!</Form.Control.Feedback>
        </Form.Group>
        
      </Row>            
      
      <Row className="mb-3">
        <Form.Group as={Col} md="4" controlId="validationCustom01">
          <Form.Label>Shipby Date</Form.Label>
          <div className="custom-date-picker">
          <DatePicker
            selected={shipByDate}
            onChange={date => setShipbyDate(date)}
            dateFormat="MM/dd/yyyy"
            placeholderText="Select Date"
            className="form-control" // Apply Bootstrap form control class
          />
        </div>
          <Form.Control.Feedback>Looks good!</Form.Control.Feedback>
        </Form.Group>
        <Form.Group as={Col} md="4" controlId="validationCustom02">
          <Form.Label>Dispatched</Form.Label>
          <Form.Select
          required
          value={dispatched} // Set the selected value
          onChange={(e) => setDispatched(e.target.value)} // Handle value change
        >
          <option value="">Select Dispatched</option>
          <option value="true">True</option>
          <option value="false">False</option>
        </Form.Select>
          <Form.Control.Feedback>Looks good!</Form.Control.Feedback>
        </Form.Group>
        
        <Form.Group as={Col} md="4" controlId="validationCustom01">
          <Form.Label>Courier</Form.Label>
          <Form.Control
            required
            type="text"
            placeholder="Courier"
                  defaultValue=""
                  value={courier}
                  onChange={(e) => setCourier(e.target.value)}
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
          <h4>List View of Orders</h4>
        </AccordionSummary>
        <AccordionDetails>
        
        <Table striped bordered hover>
            <thead>
                <tr>
                  <th></th>
                <th>
                  <span>
                    Date
                    <input
                      type="text"
                      placeholder="Search by date"
                      value={searchTermDate}
                      onChange={(e) => setSearchTermDate(e.target.value)}
                    />
                  </span>
</th>
<th>
  <span>
    Order No
    <input
      type="text"
      placeholder="Search by order no"
      value={searchTermOrderNo}
      onChange={(e) => setSearchTermOrderNo(e.target.value)}
    />
  </span>
</th>
<th>
  <span>
    Portal Order No
    <input
      type="text"
      placeholder="Search by portal order no"
      value={searchTermPortalOrderNo}
      onChange={(e) => setSearchTermPortalOrderNo(e.target.value)}
    />
  </span>
</th>
<th>
  <span>
    Portal Order Line Id
    <input
      type="text"
      placeholder="Search by portal order line id"
      value={searchTermPortalLineId}
      onChange={(e) => setSearchTermPortalLineId(e.target.value)}
    />
  </span>
</th>
<th>
  <span>
    Portal SKU
    <input
      type="text"
      placeholder="Search by portal SKU"
      value={searchTermPortalSKU}
      onChange={(e) => setSearchTermPortalSKU(e.target.value)}
    />
  </span>
</th>
<th>
  <span>
    Product Description
    <input
      type="text"
      placeholder="Search by product description"
      value={searchTermProductDescription}
      onChange={(e) => setSearchTermProductDescription(e.target.value)}
    />
  </span>
</th>
<th>
  <span>
    Ship by Date
    <input
      type="text"
      placeholder="Search by ship by date"
      value={searchTermShibByDate}
      onChange={(e) => setSearchTermShibByDate(e.target.value)}
    />
  </span>
</th>
<th>
  <span>
    Courier
    <input
      type="text"
      placeholder="Search by courier"
      value={searchTermCourier}
      onChange={(e) => setSearchTermCourier(e.target.value)}
    />
  </span>
</th>
<th>
  <span>
    Dispatched
    <input
      type="text"
      placeholder="Search by dispatched"
      value={searchTermDispatched}
      onChange={(e) => setSearchTermDispatched(e.target.value)}
    />
  </span>
</th>
<th>
  <span>
    Portal
    <input
      type="text"
      placeholder="Search by portal"
      value={searchTermPortal}
      onChange={(e) => setSearchTermPortal(e.target.value)}
    />
  </span>
</th>
<th>
  <span>
    Seller SKU
    <input
      type="text"
      placeholder="Search by seller SKU"
      value={searchTermSellerSKU}
      onChange={(e) => setSearchTermSellerSKU(e.target.value)}
    />
  </span>
</th>
<th>
  <span>
    Quantity
    <input
      type="text"
      placeholder="Search by quantity"
      value={searchTermQuantity}
      onChange={(e) => setSearchTermQuantity(e.target.value)}
    />
  </span>
</th>


              </tr>
            </thead>
            <tbody>
              {filteredData.map(order => (
                <tr key={order.orderId} onClick={() => handleRowClick(order)}>
                  <td style={{ width: '50px', textAlign: 'center' }}>
                      
                  <button
  style={{ display: 'inline-flex', justifyContent: 'center', alignItems: 'center', width: '100%', height: '100%', padding: '0', border: 'none', background: 'none' }}
  className="delete-icon"
  onClick={(e) => {
    e.stopPropagation(); // Stop propagation of the click event
    handleDelete(order.orderId); // Call handleDelete function
  }}
>
  <DeleteIcon style={{ color: '#F00' }} />
</button>

                    </td>
                  <td>{order.date}</td>
                  <td>{order.orderNo}</td>
                  <td>{order.portalOrderNo}</td>
                  <td>{order.portalOrderLineId}</td>
                  <td>{order.portalSKU}</td>
                  <td>{order.productDescription}</td>
                  <td>{order.shipByDate}</td>
                  <td>{order.courier}</td>
                  <td>{order.dispatched}</td>
                  <td>{order.portal}</td>
                  <td>{order.sellerSKU}</td>
                  <td>{order.qty}</td>
                </tr>
              ))}
            </tbody>
          </Table>
        </AccordionDetails>
      </Accordion>
  
        
        </div> 
    
  );
          
}

export default ImportOrderForm;