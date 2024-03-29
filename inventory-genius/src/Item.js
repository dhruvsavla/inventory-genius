import {useState, useEffect} from 'react';
import Button from 'react-bootstrap/Button';
import Col from 'react-bootstrap/Col';
import Form from 'react-bootstrap/Form';
import InputGroup from 'react-bootstrap/InputGroup';
import Row from 'react-bootstrap/Row';
import 'bootstrap/dist/css/bootstrap.min.css';
import "./Item.css"
import { Table, FormControl} from 'react-bootstrap';
import axios from 'axios';
import TextField from '@mui/material/TextField';
import Autocomplete from '@mui/material/Autocomplete';
import Accordion from '@mui/material/Accordion';
import AccordionSummary from '@mui/material/AccordionSummary';
import AccordionDetails from '@mui/material/AccordionDetails';
import ExpandMoreIcon from '@mui/icons-material/ExpandMore';
import * as XLSX from 'xlsx';
import DeleteIcon from '@mui/icons-material/Delete';
import { Link } from 'react-router-dom';

function Item() {
  const [validated, setValidated] = useState(false);
  const [Supplier, setSupplier] = useState("");
  const [skucode, setSKUCode] = useState("");
  const [description, setDescription] = useState("");
  const [packOf, setPackof] = useState("");
  const [parentSKU, setParentSKU] = useState("");
  const [group1, setGroup1] = useState("");
  const [group2, setGroup2] = useState("");
  const [group3, setGroup3] = useState("");
  const [sizeRange, setSizeRange] = useState("");
  const [size, setSize] = useState("");
  const [unit, setUnit] = useState("");
  const [barcode, setBarcode] = useState("");
  const [sellingPrice, setSellingPrice] = useState("");
  const [mrp, setMRP] = useState("");
  const [sellerSKUCode, setSellerSKUcode] = useState("");
  const [img, setImg] = useState("");
  const [suppliers, setSuppliers] = useState([]);
  const [apiData, setApiData] = useState([]);
  const [searchTerm, setSearchTerm] = useState("");
  const [suppliersList, setSuppliersList] = useState([]);
  const [supplierId, setSupplierId] = useState(""); 
  const [formData, setFormData] = useState({});
  const [parentSKUs, setParentSKUs] = useState([]);
  const [rowSelected, setRowSelected] = useState(false);
  const [selectedItem, setSelectedItem] = useState(null);
  const [searchTermDescription, setSearchTermDescription] = useState("");
  const [searchTermPackOf, setSearchTermPackOf] = useState("");
  const [searchTermParentSKU, setSearchTermParentSKU] = useState("");
  const [searchTermGroup1, setSearchTermGroup1] = useState("");
  const [searchTermGroup2, setSearchTermGroup2] = useState("");
  const [searchTermGroup3, setSearchTermGroup3] = useState("");
  const [searchTermSizeRange, setSearchTermSizeRange] = useState("");
  const [searchTermSize, setSearchTermSize] = useState("");
  const [searchTermUnit, setSearchTermUnit] = useState("");
  const [searchTermBarcode, setSearchTermBarcode] = useState("");
  const [searchTermSellingPrice, setSearchTermSellingPrice] = useState("");
  const [searchTermMRP, setSearchTermMRP] = useState("");
  const [searchTermSellerSKU, setSearchTermSellerSKU] = useState("");
  const [searchTermSKUCode, setSearchTermSKUCode] = useState("");
  const [searchTermImg, setSearchTermImg] = useState("");
  const [searchTermSupplier, setSearchTermSupplier] = useState("");


  const filteredData = apiData.filter(item => {
    return (
      (!searchTermSupplier || (item.supplier[0].supplierName && item.supplier[0].supplierName.toLowerCase().includes(searchTermSupplier.toLowerCase()))) &&
      (!searchTermDescription || (item.description && item.description.toLowerCase().includes(searchTermDescription.toLowerCase()))) &&
      (!searchTermPackOf || (item.packOf && item.packOf.toLowerCase().includes(searchTermPackOf.toLowerCase()))) &&
      (!searchTermGroup1 || (item.group1 && item.group1.toLowerCase().includes(searchTermGroup1.toLowerCase()))) &&
      (!searchTermGroup2 || (item.group2 && item.group2.toLowerCase().includes(searchTermGroup2.toLowerCase()))) &&
      (!searchTermGroup3 || (item.group3 && item.group3.toLowerCase().includes(searchTermGroup3.toLowerCase()))) &&
      (!searchTermSizeRange || (item.sizeRange && item.sizeRange.toLowerCase().includes(searchTermSizeRange.toLowerCase()))) &&
      (!searchTermSize || (item.size && item.size.toString().toLowerCase().includes(searchTermSize.toLowerCase()))) &&
      (!searchTermUnit || (item.unit && item.unit.toLowerCase().includes(searchTermUnit.toLowerCase()))) &&
      (!searchTermParentSKU || (item.parentSKU && item.parentSKU.toLowerCase().includes(searchTermParentSKU.toLowerCase()))) &&
      (!searchTermBarcode || (item.barcode && item.barcode.toLowerCase().includes(searchTermBarcode.toLowerCase()))) &&
      (!searchTermSellingPrice || (item.sellingPrice && item.sellingPrice.toString().toLowerCase().includes(searchTermSellingPrice.toLowerCase()))) &&
      (!searchTermMRP || (item.mrp && item.mrp.toString().toLowerCase().includes(searchTermMRP.toLowerCase()))) &&
      (!searchTermSellerSKU || (item.sellerSKUCode && item.sellerSKUCode.toLowerCase().includes(searchTermSellerSKU.toLowerCase()))) &&
      (!searchTermSKUCode || (item.skucode && item.skucode.toLowerCase().includes(searchTermSKUCode.toLowerCase()))) &&
      (searchTermImg === null || searchTermImg === '' || (item.img && item.img.toLowerCase().includes(searchTermImg.toLowerCase())))
    );
  });

  const handleSubmit = (event) => {
    event.preventDefault();
    const form = event.currentTarget;
    if (form.checkValidity() === false || !description || !skucode) {
      event.stopPropagation();
      setValidated(true); 
      return;
    }

    if (form.checkValidity() === false) {
      event.stopPropagation();
    } else {
      let formData = {
        skucode,
        description,
        packOf,
        parentSKU,
        group1,
        group2,
        group3,
        sizeRange,
        size,
        unit,
        barcode,
        sellingPrice,
        mrp,
        sellerSKUCode,
        img,
      };

      // Include suppliers only if the suppliers array is not empty
      if (suppliers.length > 0) {
        formData.suppliers = suppliers;
      } else {
        // If suppliers array is empty, include an empty array
        formData.suppliers = [];
      }

      console.log(formData)

      axios.post('http://localhost:8080/item/supplier', formData)
        .then(response => {
          console.log('POST request successful:', response);
          setValidated(false);
          setApiData([...apiData, response.data]); 
          setBarcode(""); 
          setDescription(""); 
          setGroup1(""); 
          setGroup2(""); 
          setGroup3(""); 
          setMRP(""); 
          setPackof(""); 
          setParentSKU(""); 
          setSellerSKUcode(""); 
          setSellingPrice(""); 
          setSize(""); 
          setSizeRange(""); 
          setSKUCode(""); 
          setUnit(""); 
          setSupplier("");    
          setImg("");
          setSuppliers([]); // Reset suppliers list to an empty array
          console.log('Suppliers reset to empty array:', suppliers);
        
        })
        .catch(error => {
          console.error('Error sending POST request:', error);
        });
    }
};




  useEffect(() => {
    // Fetch initial data
    fetchData();
  }, []);

  const fetchData = () => {
    axios.get('http://localhost:8080/supplier')
      .then(response => {
        setSuppliersList(response.data); 
      })
      .catch(error => {
        console.error('Error fetching supplier data:', error);
      });

      axios.get('http://localhost:8080/item/supplier')
      .then(response => {
        const filteredData = response.data.filter(item => typeof item === 'object');
        setApiData(filteredData); 
        
      })
      .catch(error => {
        console.error('Error fetching data:', error);
      });
  };

  const handleSupplierChange = (event, name) => {
    if (name) {
      const selectedSupplier = suppliersList.find(supplier => supplier.supplierName === name);
      if (selectedSupplier) {
        console.log("Selected Supplier:", selectedSupplier);
        setSupplier(selectedSupplier.supplierName); // Update the selected supplier name
        setSupplierId(selectedSupplier.supplierId); // Update the supplier ID
        
        // Add the selected supplier to the suppliers list
        setSuppliers(prevSuppliers => {
          const updatedSuppliers = [...prevSuppliers, selectedSupplier];
          console.log("Updated Suppliers:", updatedSuppliers);
          return updatedSuppliers;
        });
      } else {
        console.error("Supplier not found for name:", name);
        setSupplier(null); // Clear the selected supplier
        setSupplierId(""); // Clear the supplierId
      }
    } else {
      // Handle case when no value is selected
      setSupplier(null); // Clear the selected supplier
      setSupplierId(""); // Clear the supplierId
    }
  };
  
  

  const handleSKUCodeChange = (event, value1) => {
    if (value1) {
      console.log("Selected Parent SKU:", value1);
      setParentSKU(value1); // Update the selected SKU object
    } else {
      setParentSKU(null); // Clear the selected parent SKU if no value is selected
    }
  };
  
  const uniqueSKUCodes = [...new Set(apiData.filter(item => item.skucode !== null).map(item => item.skucode))];
  const uniqueGroup1 = [...new Set(apiData.filter(item => item.group1 !== null).map(item => item.group1))];
  const uniqueGroup2  = [...new Set(apiData.filter(item => item.group2 !== null).map(item => item.group2))];
  const uniqueGroup3  = [...new Set(apiData.filter(item => item.group3 !== null).map(item => item.group3))];

  const initialParentSKU = parentSKU && uniqueSKUCodes.includes(parentSKU) ? parentSKU : null;

  useEffect(() => {
    console.log("apidata = " , apiData); // Print apiData whenever it changes
  }, [apiData]);

  const handleRowClick = (item) => {
    axios.get(`http://localhost:8080/item/supplier/${item.itemId}`)
    .then(response => {
        // Check if the response is successful
        if (response.status !== 200) {
            throw new Error('Failed to fetch supplier');
        }
        
        // Extract supplier information from the response data
        const suppliers = response.data.suppliers;
        
        // Find the first supplier (assuming there's only one supplier in the list)
        const supplier = suppliers.length > 0 ? suppliers[0] : null;
        
        if (supplier) {
            const { supplierName, supplierId } = supplier;
            // Set supplier information
            setSupplier(supplierName);
            setSupplierId(supplierId);
        } else {
            // Handle case where no supplier is found
            console.error('No supplier found for item:', item.itemId);
        }
    })
    .catch(error => {
        console.error('Error fetching supplier:', error);
        // Handle error
    });

    setSupplier(Supplier);
    setSKUCode(item.skucode);
    setDescription(item.description);
    setPackof(item.packOf);
    setParentSKU(item.parentSKU);
    setGroup1(item.group1);
    setGroup2(item.group2);
    setGroup3(item.group3);
    setSizeRange(item.sizeRange);
    setSize(item.size);
    setUnit(item.unit);
    setBarcode(item.barcode);
    setSellingPrice(item.sellingPrice);
    setMRP(item.mrp);
    setSellerSKUcode(item.sellerSKUCode);
    setImg(item.img || '');
    setRowSelected(true);
    setSelectedItem(item);
  };

  const handleRowSubmit = () => {
    if (rowSelected && selectedItem) {
      console.log(selectedItem);
      const formData = { 
        
        // Prepare the updated item object with the changes
        ...selectedItem,
        
        skucode,
        description,
        packOf,
        parentSKU,
        group1,
        group2,
        group3,
        sizeRange,
        size,
        unit,
        barcode,
        sellingPrice,
        mrp,
        sellerSKUCode,
        img
      };

      if (suppliers.length > 0) {
        formData.suppliers = suppliers;
      } else {
        // If suppliers array is empty, include an empty array
        formData.suppliers = [];
      }
  
      axios.put(`http://localhost:8080/item/supplier/${selectedItem.itemId}`, formData)
        .then(response => {
          console.log('PUT request successful:', response);
          setApiData(prevData => prevData.map(item => item.itemId === selectedItem.itemId ? response.data : item)); // Update the specific item
  
          setValidated(false);
          setRowSelected(false);
          setSelectedItem(response.data); // Update selectedItem with the response data
          // Clear form fields
          setSupplier("");
          setSKUCode("");
          setDescription("");
          setPackof("");
          setParentSKU("");
          setGroup1("");
          setGroup2("");
          setGroup3("");
          setSizeRange("");
          setSize("");
          setUnit("");
          setSellerSKUcode("");
          setBarcode("");
          setSellingPrice("");
          setMRP("");
          setImg("");
          setSuppliers([]);
        })
        .catch(error => {
          console.error('Error sending PUT request:', error);
        });
    }
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
              supplierId: supplierId,
              skucode: skucode,
              description: description,
              packOf: packOf,
              parentSKU: parentSKU,
              group1: group1,
              group2: group2,
              group3: group3,
              sizeRange: sizeRange,
              size: size,
              unit: unit,
              barcode: barcode,
              sellingPrice: sellingPrice,
              mrp: mrp,
              sellerSKUCode: sellerSKUCode,
      
            };
          console.log(formattedData)
            postData(formattedData);
        });
    };

    reader.readAsBinaryString(file);
  };

  const postData = (data) => {
    axios.post('http://localhost:8080/items/' + supplierId, data)
        .then(response => {
            // Handle successful response
            console.log('Data posted successfully:', response);
        })
        .catch(error => {
            // Handle error
            console.error('Error posting data:', error);
        });
  };

  const handleParentSKUChange = (event) => {
    setParentSKU(event.target.value); // Update the parent SKU value
  };

  const handleDelete = (id) => {
    console.log("Deleting row with id:", id);
    // Remove the row from the table
  
    axios.delete(`http://localhost:8080/item/supplier/${id}`)
    .then(response => {
      // Handle success response
      console.log('Row deleted successfully.');
      setApiData(prevData => prevData.filter(row => row.itemId !== id));

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
            <h1>Item</h1>
        </div>

        <Accordion defaultExpanded>
          <AccordionSummary className='acc-summary'
              expandIcon={<ExpandMoreIcon />}
              aria-controls="panel3-content"
              id="panel3-header"
              sx={{ backgroundColor: '#E5E7E9' }} 
            >
          <h4>Item Form</h4>
          </AccordionSummary>
        <AccordionDetails>
                
        <Form noValidate validated={validated} onSubmit={handleSubmit}>
          <Row className="mb-3">      
          <Form.Group as={Col} md="4" controlId="validationCustom01">
            <Form.Label>Supplier</Form.Label>
            <Form.Select
              required
              onChange={(e) => handleSupplierChange(e, e.target.value)}
              value={Supplier} // Change 'supplier' to 'Supplier'
            >
            <option value="">Select Supplier</option>
            {suppliersList.map((supplier) => (
              <option key={supplier.supplierId} value={supplier.supplierName}>
                {supplier.supplierName}
              </option>
            ))}
          </Form.Select>
          <Link to="/Supplier"><span style = {{float:"right", fontSize:"small", marginTop:"1%", marginRight:"1%"}}>+ add supplier</span></Link>
            <Form.Control.Feedback>Looks good!</Form.Control.Feedback>
          </Form.Group>

          <Form.Group as={Col} md="4" controlId="validationCustom02">
            <Form.Label>SKUCode</Form.Label>
            <Form.Control
              required
              type="text"
              placeholder="SKUCode"
              defaultValue=""
              value={skucode }
              onChange={(e) => setSKUCode(e.target.value)}                  
            />
            <Form.Control.Feedback>Looks good!</Form.Control.Feedback>
          </Form.Group>
                  
          <Form.Group as={Col} md="4" controlId="validationCustom01">
            <Form.Label>Description</Form.Label>
              <Form.Control
                required
                type="text"
                placeholder="Description"
                defaultValue=""
                value={description }
                onChange={(e) => setDescription(e.target.value)} 
              />
            <Form.Control.Feedback>Looks good!</Form.Control.Feedback>
          </Form.Group>
        </Row>
              
        <Row className="mb-3">
         <Form.Group as={Col} md="4" controlId="validationCustom02">
          <Form.Label>Packof</Form.Label>
            <Form.Control
              required
              type="text"
              placeholder="Packof"
              defaultValue=""
              value={ packOf}
              onChange={(e) => setPackof(e.target.value)} 
            />
          <Form.Control.Feedback>Looks good!</Form.Control.Feedback>
        </Form.Group>
                
        <Form.Group as={Col} md="4" controlId="validationCustom02">
          <Form.Label>ParentSKU</Form.Label> 

          <input 
  className='form-control'
    list="uniqueSKUCodes" 
    onChange={(e) => setParentSKU(e.target.value)} 
    placeholder="Parent SKUCode" 
    value={parentSKU} 
  />
  <datalist id="uniqueSKUCodes"> 
    {uniqueSKUCodes.map((op) => (
      <option key={op}>{op}</option> ))}
  </datalist>
            
          <Form.Control.Feedback>Looks good!</Form.Control.Feedback>
        </Form.Group>
                
        <Form.Group as={Col} md="4" controlId="validationCustom02">
  <Form.Label>Group1</Form.Label>
  <input 
  className='form-control'
    list="uniqeGroup1" 
    onChange={(e) => setGroup1(e.target.value)} 
    placeholder="Group 1" 
    value={group1} 
  />
  <datalist id="uniqeGroup1"> 
    {uniqueGroup1.map((op) => (
      <option key={op}>{op}</option> ))}
  </datalist>
  <Form.Control.Feedback>Looks good!</Form.Control.Feedback>
</Form.Group>

      </Row>
            
      <Row className="mb-3">
        <Form.Group as={Col} md="4" controlId="validationCustom01">
          <Form.Label>Group2</Form.Label>
          <input 
  className='form-control'
    list="uniqeGroup2" 
    onChange={(e) => setGroup2(e.target.value)} 
    placeholder="Group 2" 
    value={group2} 
  />
  <datalist id="uniqeGroup2"> 
    {uniqueGroup2.map((op) => (
      <option key={op}>{op}</option> ))}
  </datalist>
          <Form.Control.Feedback>Looks good!</Form.Control.Feedback>
        </Form.Group>
                
        <Form.Group as={Col} md="4" controlId="validationCustom02">
          <Form.Label>Group3</Form.Label>
          <input 
  className='form-control'
    list="uniqeGroup3" 
    onChange={(e) => setGroup3(e.target.value)} 
    placeholder="Group 3" 
    value={group3} 
  />
  <datalist id="uniqeGroup3"> 
    {uniqueGroup3.map((op) => (
      <option key={op}>{op}</option> ))}
  </datalist>
          <Form.Control.Feedback>Looks good!</Form.Control.Feedback>
        </Form.Group>
                
        <Form.Group as={Col} md="4" controlId="validationCustom01">
          <Form.Label>SizeRange</Form.Label>
            <Form.Control
              required
              type="text"
              placeholder="SizeRange"
              defaultValue=""
              value={ sizeRange}
              onChange={(e) => setSizeRange(e.target.value)} 
            />
          <Form.Control.Feedback>Looks good!</Form.Control.Feedback>
        </Form.Group>
      </Row>
              
      <Row className="mb-3"> 
        <Form.Group as={Col} md="4" controlId="validationCustom02">
          <Form.Label>Size</Form.Label>
            <Form.Control
              required
              type="text"
              placeholder="Size"
              defaultValue=""
              value={size }
              onChange={(e) => setSize(e.target.value)} 
            />
          <Form.Control.Feedback>Looks good!</Form.Control.Feedback>
        </Form.Group>
                
        <Form.Group as={Col} md="4" controlId="validationCustom01">
          <Form.Label>Unit</Form.Label>
            <Form.Control
              required
              type="text"
              placeholder="Unit"
              defaultValue=""
              value={unit }
              onChange={(e) => setUnit(e.target.value)} 
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
              value={sellerSKUCode }
              onChange={(e) => setSellerSKUcode(e.target.value)} 
            />
          <Form.Control.Feedback>Looks good!</Form.Control.Feedback>
        </Form.Group>
                
      </Row>
                   
      <Row className="mb-3">
        <Form.Group as={Col} md="4" controlId="validationCustom01">
          <Form.Label>Barcode</Form.Label>
            <Form.Control
              required
              type="text"
              placeholder="Barcode"
              defaultValue=""
              value={ barcode}
              onChange={(e) => setBarcode(e.target.value)} 
            />
          <Form.Control.Feedback>Looks good!</Form.Control.Feedback>
        </Form.Group>
        
        <Form.Group as={Col} md="4" controlId="validationCustom02">
          <Form.Label>SellingPrice</Form.Label>
            <Form.Control
              required
              type="text"
              placeholder="SellingPrice"
              defaultValue=""
              value={sellingPrice }
              onChange={(e) => setSellingPrice(e.target.value)} 
            />
          <Form.Control.Feedback>Looks good!</Form.Control.Feedback>
        </Form.Group>
                
        <Form.Group as={Col} md="4" controlId="validationCustom01">
          <Form.Label>MRP</Form.Label>
            <Form.Control
              required
              type="text"
              placeholder="MRP"
              defaultValue=""
              value={ mrp}
              onChange={(e) => setMRP(e.target.value)} 
            />
          <Form.Control.Feedback>Looks good!</Form.Control.Feedback>
        </Form.Group>
        </Row>
        <Row className="mb-3">
        <Form.Group as={Col} md="4" controlId="validationCustom01">
  <Form.Label>Image URL</Form.Label>
  <Form.Control
    required
    type="text"
    placeholder="Image URL"
    value={img}
    onChange={(e) => setImg(e.target.value)}
  />
  <Form.Control.Feedback>Looks good!</Form.Control.Feedback>
</Form.Group>
<Form.Group>
{img && (
      <div>
      
        <img src={img} alt="item image" style={{ width:"30%", height:"10%", margin:"1%" }} />
      </div>
    )}
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
        <h4>List View of Item</h4>
      </AccordionSummary>
          <AccordionDetails>
        {apiData && (
  <div style={{ overflowX: 'auto' }}> 
      <Table className='custom-table'>
        <thead>
          <tr>
            <th></th>
            <th>Supplier
            <span style={{ margin: '0 10px' }}><input
                  type="text"
                  placeholder="Search by supplier"
                  value={searchTermSupplier}
                  onChange={(e) => setSearchTermSupplier(e.target.value)}
                /></span>
            </th>
            <th>Description 
            <span style={{ margin: '0 10px' }}><input
                  type="text"
                  placeholder="Search by Description"
                  value={searchTermDescription}
                  onChange={(e) => setSearchTermDescription(e.target.value)}
                /></span>
            </th>
            <th>Pack Of
            <span style={{ margin: '0 10px' }}><input
                  type="text"
                  placeholder="Search by Pack of"
                  value={searchTermPackOf}
                  onChange={(e) => setSearchTermPackOf(e.target.value)}
                /></span>
            </th>
            <th>Parent SKU
            <span style={{ margin: '0 10px' }}><input
                  type="text"
                  placeholder="Search by Parent SKUCode"
                  value={searchTermParentSKU}
                  onChange={(e) => setSearchTermParentSKU(e.target.value)}
                /></span>
            </th>
            <th>Group 1
            <span style={{ margin: '0 10px' }}><input
                  type="text"
                  placeholder="Search by group1"
                  value={searchTermGroup1}
                  onChange={(e) => setSearchTermGroup1(e.target.value)}
                /></span>
            </th>
            <th>Group 2
            <span style={{ margin: '0 10px' }}><input
                  type="text"
                  placeholder="Search by group2"
                  value={searchTermGroup2}
                  onChange={(e) => setSearchTermGroup2(e.target.value)}
                /></span>
            </th>
            <th>Group 3
            <span style={{ margin: '0 10px' }}><input
                  type="text"
                  placeholder="Search by group3"
                  value={searchTermGroup3}
                  onChange={(e) => setSearchTermGroup3(e.target.value)}
                /></span>
            </th>
            <th>Size Range
            <span style={{ margin: '0 10px' }}><input
                  type="text"
                  placeholder="Search by size range"
                  value={searchTermSizeRange}
                  onChange={(e) => setSearchTermSizeRange(e.target.value)}
                /></span>
            </th>
            <th>Size
            <span style={{ margin: '0 10px' }}><input
                  type="text"
                  placeholder="Search by Size"
                  value={searchTermSize}
                  onChange={(e) => setSearchTermSize(e.target.value)}
                /></span>
            </th>
            <th>Unit
            <span style={{ margin: '0 10px' }}><input
                  type="text"
                  placeholder="Search by Unit"
                  value={searchTermUnit}
                  onChange={(e) => setSearchTermUnit(e.target.value)}
                /></span>
            </th>
            <th>Barcode
            <span style={{ margin: '0 10px' }}><input
                  type="text"
                  placeholder="Search by Barcode"
                  value={searchTermBarcode}
                  onChange={(e) => setSearchTermBarcode(e.target.value)}
                /></span>
            </th>
            <th>Selling Price
            <span style={{ margin: '0 10px' }}><input
                  type="text"
                  placeholder="Search by Selling Price"
                  value={searchTermSellingPrice}
                  onChange={(e) => setSearchTermSellingPrice(e.target.value)}
                /></span>
            </th>
            <th>MRP
            <span style={{ margin: '0 10px' }}><input
                  type="text"
                  placeholder="Search by MRP"
                  value={searchTermMRP}
                  onChange={(e) => setSearchTermMRP(e.target.value)}
                /></span>
            </th>
            <th>Seller SKU Code
            <span style={{ margin: '0 10px' }}><input
                  type="text"
                  placeholder="Search by Seller SKUCode"
                  value={searchTermSellerSKU}
                  onChange={(e) => setSearchTermSellerSKU(e.target.value)}
                /></span>
            </th>
            <th>SKU Code
            <span style={{ margin: '0 10px' }}><input
                  type="text"
                  placeholder="Search by SKUCode"
                  value={searchTermSKUCode}
                  onChange={(e) => setSearchTermSKUCode(e.target.value)}
                /></span>
            </th>
            <th>Image URL
            <span style={{ margin: '0 10px' }}><input
                  type="text"
                  placeholder="Search by img-url"
                  value={searchTermImg}
                  onChange={(e) => setSearchTermImg(e.target.value)}
                /></span>
            </th>
          </tr>
        </thead>
        <tbody>
          {filteredData.map(item => (
            <tr key={item.itemId} onClick={() => handleRowClick(item)}> 
            <td style={{ width: '50px', textAlign: 'center' }}>
                      
            <button
  style={{ display: 'inline-flex', justifyContent: 'center', alignItems: 'center', width: '100%', height: '100%', padding: '0', border: 'none', background: 'none' }}
  className="delete-icon"
  onClick={(e) => {
    e.stopPropagation(); // Stop propagation of the click event
    handleDelete(item.itemId); // Call handleDelete function
  }}
>
  <DeleteIcon style={{ color: '#F00' }} />
</button>

                    </td>
                    <td>
    {/* Check if item.suppliers is defined and not null */}
    {item.suppliers && item.suppliers.length > 0 ? (
        // If item.suppliers is defined and not null and has a length greater than 0, map through each supplier
        item.suppliers.map(supplier => (
            // Displaying the supplierName for each supplier
            <div key={supplier.supplierId}>{supplier.supplierName}</div>
        ))
    ) : (
        // If item.suppliers is undefined, null, or has a length of 0, render an empty string
        ''
    )}
</td>


              <td>{item.description}</td>
              <td>{item.packOf}</td>
              <td>{item.parentSKU}</td>
              <td>{item.group1}</td>
              <td>{item.group2}</td>
              <td>{item.group3}</td>
              <td>{item.sizeRange}</td>
              <td>{item.size}</td>
              <td>{item.unit}</td>
              <td>{item.barcode}</td>
              <td>{item.sellingPrice}</td>
              <td>{item.mrp}</td>
              <td>{item.sellerSKUCode}</td>
              <td>{item.skucode}</td>
              <td>{item.img ? item.img : ''}</td>

            </tr>
          ))}
        </tbody>
      </Table>
</div>
      )}
          </AccordionDetails>
          </Accordion>

            </div>
          
  );
}

export default Item;