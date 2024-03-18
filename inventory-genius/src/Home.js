import React from 'react'
import Header from './Header'
import "./Home.css"
import Button from 'react-bootstrap/Button';
import 'bootstrap/dist/css/bootstrap.min.css';
import ImportOrderForm from './ImportOrderForm';
import PackingList from './PackingList';
import PickList from './PickList';
import Stock from './Stock';
import Bom from './Bom';
import Item from './Item';
import ItemPortalMapping from './ItemPortalMapping';
import Returns from './Returns';
import StockInward from './StockInward';
import { Link } from 'react-router-dom';

function Home() {
  return (
      <div className='home'>
          <div className='background-image'>
            <img src = "https://www.simplilearn.com/ice9/free_resources_article_thumb/What_Is_Inventory_Management.jpg"/>
            <div className='content'>
                  <h1>Welcome to the inventory genius</h1>
                  <Link to = "/supplier">
                  <Button variant="danger">Add Supplier</Button>{' '}
                  </Link>
                  <Link to = "/item">
                  <Button variant="danger">Add Item</Button>{' '}
                  </Link>
                  <Link to = "/item">
                  <Button variant="danger">Order</Button>{' '}
                  </Link>
                  <Link to = "/item">
                  <Button variant="danger">Returns</Button>{' '}
                  </Link>
            </div>
             
          </div>
          <Bom />
          <ImportOrderForm />
          <Item />
          <ItemPortalMapping />
          <PackingList />
          <PickList />
          <Returns />
          <Stock />
          <StockInward />
          
      </div>
  )
}

export default Home