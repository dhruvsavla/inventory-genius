import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { Document, Page, Text, View, StyleSheet, PDFDownloadLink } from '@react-pdf/renderer';
import { saveAs } from 'file-saver';
import './PickList.css'; // Import CSS file for custom styling

const styles = StyleSheet.create({
  page: {
    flexDirection: 'row',
    padding: 20,
  },
  order: {
    marginBottom: 20,
  },
});

function PickList() {
  const [orders, setOrders] = useState([]);
  const [selectedOrders, setSelectedOrders] = useState([]);
  const [showSelectedOrders, setShowSelectedOrders] = useState(false);

  useEffect(() => {
    const fetchOrders = async () => {
      try {
        const response = await axios.get(`http://localhost:8080/orders`);
        setOrders(response.data);
        console.log("orders:", JSON.stringify(response.data, null, 2));
      } catch (error) {
        console.error('Error fetching orders:', error);
      }
    };

    fetchOrders();
  }, []);

  const handleCheckboxChange = (orderId) => {
    const orderIndex = selectedOrders.findIndex((order) => order.orderId === orderId);

    if (orderIndex === -1) {
      // Order not selected yet, add it
      setSelectedOrders([...selectedOrders, orders.find((order) => order.orderId === orderId)]);
    } else {
      // Order already selected, remove it
      setSelectedOrders([...selectedOrders.slice(0, orderIndex), ...selectedOrders.slice(orderIndex + 1)]);
    }
  };

  const handlePrint = () => {
    setShowSelectedOrders(true);
  };

  const resetSelectedOrders = () => {
    setSelectedOrders([]);
    setShowSelectedOrders(false);
  };

  const generatePDF = () => {
    const pdfData = (
      <Document>
        {selectedOrders.map((order, index) => (
          <Page key={index} size="A4" style={styles.page}>
            <View style={styles.order}>
              <Text>Order No: {order.orderNo}</Text>
              <Text>Date: {order.date}</Text>
              <Text>SellerSKU: {order.sellerSKU}</Text>
              <Text>Quantity: {order.qty}</Text>
              <Text>Portal: {order.portal}</Text>
              <Text>Items:</Text>
              {order.items.map((item, itemIndex) => (
                <Text key={itemIndex}>
                  - {item.description}({item.skucode})
                </Text>
              ))}
              <Text>Bin Number: </Text>{order.items.map((item, itemIndex) => (
                  <Text key={itemIndex}>
                    {item.storage ? item.storage.binNumber : 'N/A'}
                    {itemIndex !== order.items.length - 1 ? ', ' : ''}
                  </Text>
                ))}
              <Text>Rack Number: </Text>{order.items.map((item, itemIndex) => (
                  <Text key={itemIndex}>
                    {item.storage ? item.storage.rackNumber : 'N/A'}
                    {itemIndex !== order.items.length - 1 ? ', ' : ''}
                  </Text>
                ))}
            </View>
          </Page>
        ))}
      </Document>
    );
  
    return pdfData;
  };

  return (
    <div className="picklist-container">
      <div className="order-cards-container">
      {orders.map((order, index) => (
  <label key={order.orderId} className={`order-card ${selectedOrders.some((selectedOrder) => selectedOrder.orderId === order.orderId) ? 'selected' : ''}`}>
    <input
      type="checkbox"
      checked={selectedOrders.some((selectedOrder) => selectedOrder.orderId === order.orderId)}
      onChange={() => handleCheckboxChange(order.orderId)}
    />
    <div>
      <h2>Order No: {order.orderNo}</h2>
      <p>Date: {order.date}</p>
      <p>SellerSKU: {order.sellerSKU}</p>
      <p>
        Items:
        {order.items.map((item, itemIndex) => (
          <span key={itemIndex}>
            {item.description}({item.skucode})
            {itemIndex !== order.items.length - 1 ? ', ' : ''}
          </span>
        ))}
      </p>
      <p>
        Rack No:
        {order.items.map((item, itemIndex) => (
          <span key={itemIndex}>
            {item.storage ? item.storage.rackNumber : 'N/A'}
            {itemIndex !== order.items.length - 1 ? ', ' : ''}
          </span>
        ))}
      </p>
      <p>
        Bin No: 
        {order.items.map((item, itemIndex) => (
          <span key={itemIndex}>
            {item.storage ? item.storage.binNumber : 'N/A'}
            {itemIndex !== order.items.length - 1 ? ', ' : ''}
          </span>
        ))}
      </p>

      <p>Quantity: {order.qty}</p>
      <p>Portal: {order.portal}</p>
    </div>
  </label>
))}

      </div>
      
      <div className="button-container">
      
        {showSelectedOrders ? (
          
          <div className="selected-orders">
            <h2 style={{textAlign:"center", marginTop:"2%"}}>Selected Orders</h2>
            {selectedOrders.map((order, index) => (
  <div key={index} className={`selected-order ${index % 2 === 0 ? 'even' : 'odd'}`}>
    <p>Order No: {order.orderNo}</p>
    <p>Date: {order.date}</p>
    <p>SellerSKU: {order.sellerSKU}</p>
    <p>
      Items:
      {order.items.map((item, itemIndex) => (
        <span key={itemIndex}>
          {item.description}({item.skucode})
          {itemIndex !== order.items.length - 1 ? ', ' : ''}
        </span>
      ))}
    </p>
    <p>
      Rack No:
      {order.items.map((item, itemIndex) => (
        <span key={itemIndex}>
          {item.storage ? item.storage.rackNumber : 'N/A'}
          {itemIndex !== order.items.length - 1 ? ', ' : ''}
        </span>
      ))}
    </p>
    <p>
      Bin No: 
      {order.items.map((item, itemIndex) => (
        <span key={itemIndex}>
          {item.storage ? item.storage.binNumber : 'N/A'}
          {itemIndex !== order.items.length - 1 ? ', ' : ''}
        </span>
      ))}
    </p>
    <p>Quantity: {order.qty}</p>
    <p>Portal: {order.portal}</p>
    {index !== selectedOrders.length - 1 && (
      <hr className={`horizontal-line-${(index % 2) + 1}`} />
    )} {/* Add horizontal line if not the last order */}
  </div>
))}
            <div className='reset-buttons'>
            <button onClick={resetSelectedOrders}>Reset Selection</button>
            <PDFDownloadLink className="download-button" document={generatePDF()} fileName="picklist.pdf">
              {({ loading }) => (loading ? 'Loading...' : 'Download PDF')}
            </PDFDownloadLink>
            </div>
          </div>
        ) : (
          <>
            <button className="download-button" onClick={handlePrint}>Print Selected Orders</button>
            <PDFDownloadLink className="download-button" document={generatePDF()} fileName="picklist.pdf">
              {({ loading }) => (loading ? 'Loading...' : 'Download PDF')}
            </PDFDownloadLink>
          </>
          
        )}
        
      </div>
    </div>
  );
  
}

export default PickList;
