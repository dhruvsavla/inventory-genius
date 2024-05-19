import { useState, useEffect } from 'react';
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
import { toast, ToastContainer } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';

function Storage() {
  const [validated, setValidated] = useState(false);
  const [binNumber, setBin] = useState("");
  const [rackNumber, setRack] = useState("");
  const [skucode, setSkucode] = useState("");
  const [qty, setQty] = useState("");
  const [apiData, setApiData] = useState([]); 
  const [rowSelected, setRowSelected] = useState(false);
  const [selectedItem, setSelectedItem] = useState(null);
  const [searchTerm, setSearchTerm] = useState("");
  const [skuList, setSkuList] = useState([]);
  const [searchTermRack, setSearchTermRack] = useState("");
  const [searchTermBin, setSearchTermBin] = useState("");
  const [searchTermSKU, setSearchTermSKU] = useState("");
  const [searchTermQty, setSearchTermQty] = useState("");
  const [itemImg, setItemImg] = useState("");

  const filteredData = apiData.filter(supplier => {
    return (
      (supplier.binNumber && supplier.binNumber.toLowerCase().includes(searchTermBin.toLowerCase())) &&
      (supplier.rackNumber && supplier.rackNumber.toLowerCase().includes(searchTermRack.toLowerCase())) &&
      (supplier.skucode && supplier.skucode.toLowerCase().includes(searchTermSKU.toLowerCase())) &&
      (searchTermQty === null || searchTermQty === '' || (supplier.qty && supplier.qty.toLowerCase().includes(searchTermQty.toLowerCase())))
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
                bin: item.binNumber,
                rack: item.rackNumber,
                skucde: item.skucode,
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
    axios.get(`http://localhost:8080/item/supplier/search/skucode/${skucode}`)
      .then(response => {
        console.log("data = " + response.data);
        if (response.data) {
          const itemId = response.data.itemId;
          const formData = {
            binNumber,
            rackNumber,
            skucode,
            qty
          };
          console.log('form data: ', formData);
          axios.post(`http://localhost:8080/storage/${itemId}`, formData)
            .then(response => {
              console.log('POST request successful:', response);
              toast.success('Storage added successfully', {
                autoClose: 2000 // Close after 2 seconds
              });
              setValidated(false);
              setApiData([...apiData, response.data]);
              setBin('');
              setRack('');
              setSkucode('');
              setQty('');
              setItemImg('');
            })
            .catch(error => {
              console.error('Error sending POST request:', error);
              toast.error('Failed to add Storage: ' + error.response.data.message);
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
      binNumber,
      rackNumber,
      skucode,
      qty
    };
    console.log('form data: ', formData)
    console.log("id: ", selectedItem.storageId)
    axios.put(`http://localhost:8080/storage/${selectedItem.storageId}`, formData)
      .then(response => {
        
        console.log('PUT request successful:', response);
        toast.success('Storage updated successfully', {
          autoClose: 2000 // Close after 2 seconds
        });
        setApiData(prevData => prevData.map(item => item.storageId === selectedItem.storageId ? response.data : item)); // Update the specific item

        setValidated(false);
        setRowSelected(false);
        setBin("");
        setRack("");
        setSkucode("");
        setQty("");
      })
      .catch(error => {
        console.error('Error sending PUT request:', error);
        toast.error('Failed to update Storage: ' + error.response.data.message);
      });
  }
};


const handleRowClick = (storage) => {
  setBin(storage.binNumber);
  setRack(storage.rackNumber);
  setSkucode(storage.skucode);
  setQty(storage.qty);
  setRowSelected(true);
  setSelectedItem(storage);
};

const handleSearchChange = (event) => {
  setSearchTerm(event.target.value.toLowerCase()); // Convert search term to lowercase
};

useEffect(() => {
  axios.get('http://localhost:8080/storage') 
    .then(response => setApiData(response.data))
      .catch(error => console.error(error));
    console.log("apidata = "+JSON.stringify(apiData))

   
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
    axios.post('http://localhost:8080/storage', data)
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

  axios.delete(`http://localhost:8080/storage/${id}`)
  .then(response => {
    // Handle success response
    console.log('Row deleted successfully.');
    toast.success('Storage deleted successfully', {
      autoClose: 2000 // Close after 2 seconds
    });
    setApiData(prevData => prevData.filter(row => row.storageId !== id));

  })
  .catch(error => {
    // Handle error
    console.error('Error deleting row:', error);
    toast.error('Failed to delete Storage: ' + error.response.data.message);
  });

  console.log("After deletion, apiData:", apiData);
};

const getImg = (skucode) => {
  axios.get(`http://localhost:8080/item/supplier/search/skucode/${skucode}`)
    .then(response => {
      setItemImg(response.data.img);
    })
    .catch(error => {
      // Handle error
      console.error('Error getting img:', error);
    });
}

    return (
      <div>
        <ToastContainer position="top-right" />
        <div className='title'>
          <h1>Storage</h1>
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
          <Form.Group as={Col} md="4" controlId="validationCustom02">
          <Form.Label>Rack No</Form.Label>
          <Form.Control
                  required
                  type="text"
                  placeholder="Rack No"
                  name="Rack no"
                  value={rackNumber}
                  onChange={(e) => setRack(e.target.value)}
                />
          <Form.Control.Feedback>Looks good!</Form.Control.Feedback>
        </Form.Group>
            <Form.Group as={Col} md="4" controlId="validationCustom01">
              <Form.Label>Bin No</Form.Label>
              <Form.Control
                  required
                  type="text"
                  placeholder="Bin No"
                  name="Bin No"
                  value={binNumber}
                  onChange={(e) => setBin(e.target.value)}
                />
          <Form.Control.Feedback>Looks good!</Form.Control.Feedback>
        </Form.Group>
        
       
</Row>
      <Row className="mb-3">

      <Form.Group as={Col} md="4" controlId="validationCustom01">
        <Form.Label>SKUCode</Form.Label>
        <Form.Select
          required
          onChange={(e) => {
            setSkucode(e.target.value); // Update the state with the selected value
            // Call your additional function here
            getImg(e.target.value);
          }}
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

        <Form.Group as={Col} md="4" controlId="validationCustom01">
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
        {itemImg && (
        <img alt = "item image" src = {itemImg} className='rotating1' style={{width: "200px", height: "150px", marginTop: "-50px", marginLeft: "100px"}}></img>
        )}
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
          <h4>List View of Storage</h4>
        </AccordionSummary>
        <AccordionDetails>
        
        <Table striped bordered hover>
            <thead>
              <tr>
                <th></th>
                <th>Rack No
                <span style={{ margin: '0 10px' }}><input
                  type="text"
                  placeholder="Search by rack"
                  value={searchTermRack}
                  onChange={(e) => setSearchTermRack(e.target.value)}
                /></span>
                </th>
                <th>Bin No
                <span style={{ margin: '0 10px' }}><input
                  type="text"
                  placeholder="Search by bin"
                  value={searchTermBin}
                  onChange={(e) => setSearchTermBin(e.target.value)}
                /></span>
                </th>
                
                <th>SKUCode
                <span style={{ margin: '0 10px' }}><input
                  type="text"
                  placeholder="Search by SKU"
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
  {filteredData.map(storage => (
    <tr key={storage.id} onClick={() => handleRowClick(storage)}>
      <td style={{ width: '50px', textAlign: 'center' }}>
        <button
          style={{ display: 'inline-flex', justifyContent: 'center', alignItems: 'center', width: '100%', height: '100%', padding: '0', border: 'none', background: 'none' }}
          className="delete-icon"
          onClick={(e) => {
            e.stopPropagation(); // Stop propagation of the click event
            handleDelete(storage.storageId); // Call handleDelete function
          }}
        >
          <DeleteIcon style={{ color: '#F00' }} />
        </button>
      </td>
      <td>{storage.rackNumber}</td>
      <td>{storage.binNumber}</td>
      <td>{storage.skucode}</td>
      <td>{storage.qty !== null ? storage.qty : ''}</td> {/* Conditionally render qty */}
    </tr>
  ))}
</tbody>
          </Table>
        </AccordionDetails>
      </Accordion>
            </div>
  );
}

export default Storage;