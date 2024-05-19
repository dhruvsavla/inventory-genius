import React from 'react'
import Button from 'react-bootstrap/Button';
import Container from 'react-bootstrap/Container';
import Form from 'react-bootstrap/Form';
import Nav from 'react-bootstrap/Nav';
import Navbar from 'react-bootstrap/Navbar';
import NavDropdown from 'react-bootstrap/NavDropdown';
import 'bootstrap/dist/css/bootstrap.min.css';
import "./Header.css"
import { Link } from 'react-router-dom';

function Header() {
  return (
      <div className='header'>
          <Navbar expand="lg" className="bg-body-tertiary">
              <Container fluid>
                  <Link to ="/">
                      <Navbar.Brand href="#">TechJyot</Navbar.Brand>
                      </Link>
            <Navbar.Toggle aria-controls="navbarScroll" />
            <Navbar.Collapse id="navbarScroll">
            <Nav
                className="me-auto my-2 my-lg-0"
                style={{ maxHeight: '100px' }}
                navbarScroll
            >
            
                <Nav.Link href="/supplier">Supplier</Nav.Link>
                <Nav.Link href="/item">Item</Nav.Link>
                <Nav.Link href="/itemportalmapping">Item Portal Mapping</Nav.Link>
                <Nav.Link href="/bom">BOM</Nav.Link>
                <Nav.Link href="/importorderform">Import Order Form</Nav.Link>
                <Nav.Link href="/picklist">PickList</Nav.Link>
                <Nav.Link href="/packinglist">PackingList</Nav.Link>
                <Nav.Link href="/stock">Stock</Nav.Link>
                <Nav.Link href="/stockinward">Stock Inward</Nav.Link>
                <Nav.Link href="/storage">Storage</Nav.Link>
                {/* <Nav.Link href="/dispatch">Dispatch</Nav.Link> */}
                <Nav.Link href="/returns">Returns</Nav.Link>
            </Nav>
            {/* <Form className="d-flex">
                <Form.Control
                type="search"
                placeholder="Search"
                className="me-2"
                aria-label="Search"
                />
                <Button variant="outline-success">Search</Button>
            </Form> */}
            </Navbar.Collapse>
        </Container>
    </Navbar>
    </div>
  )
}

export default Header