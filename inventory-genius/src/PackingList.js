import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { Document, Page, View, Text, StyleSheet, PDFDownloadLink } from '@react-pdf/renderer';
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
  selectedOrder: {
    backgroundColor: '#f0f0f0',
    padding: 10,
    borderRadius: 5,
    marginBottom: 10,
  },
  selectedOrderText: {
    fontWeight: 'bold',
    marginBottom: 10,
  },
});

function PackingList() {
  const [ordersWithItems, setOrdersWithItems] = useState([]);
  const [selectedOrders, setSelectedOrders] = useState([]);
  const [showSelectedOrders, setShowSelectedOrders] = useState(false);
  const [pdfData, setPdfData] = useState(null);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const ordersWithItemsResponse = await axios.get('http://localhost:8080/orders/with-items');
        setOrdersWithItems(ordersWithItemsResponse.data);
      } catch (error) {
        console.error('Error fetching data:', error);
      }
    };

    fetchData();
  }, []);

  const groupedOrders = ordersWithItems.reduce((acc, order) => {
    const key = order.portalOrderNo;
    if (!acc[key]) {
      acc[key] = [];
    }
    acc[key].push(order);
    return acc;
  }, {});

  const handleCheckboxChange = (portalOrderNo) => {
    const updatedSelectedOrders = selectedOrders.includes(portalOrderNo)
      ? selectedOrders.filter((selectedOrder) => selectedOrder !== portalOrderNo)
      : [...selectedOrders, portalOrderNo];
    setSelectedOrders(updatedSelectedOrders);
  };

  const handlePrint = () => {
    setShowSelectedOrders(true);
    const pdfData = generatePDF();
    setPdfData(pdfData);
    console.log(pdfData);
  };

  const resetSelectedOrders = () => {
    setSelectedOrders([]);
    setShowSelectedOrders(false);
    setPdfData(null);
  };

  const generatePDF = () => {
    return (
      <Document>
        {selectedOrders.map((portalOrderNo, index) => {
          const orderDetails = groupedOrders[portalOrderNo];
          return (
            <Page key={index} size="A4" style={styles.page}>
              <View style={styles.order}>
                <Text style={styles.selectedOrderText}>Portal Order No: {portalOrderNo}</Text>
                {orderDetails.map(order => (
                  <View key={order.orderId} style={styles.selectedOrder}>
                    <View style={styles.field}>
                      <Text>Portal:</Text>
                      <Text>{order.portal}</Text>
                    </View>
                    <View style={styles.field}>
                      <Text>Ship by Date:</Text>
                      <Text>{order.shipByDate}</Text>
                    </View>
                    <View style={styles.field}>
                      <Text>Items:</Text>
                      <View style={{ marginLeft: 10 }}>
                        {order.items.map(item => (
                          <View key={item.itemId}>
                            <Text>SKU Code: {item.skucode}</Text>
                            <Text>Description: {item.description}</Text>
                            <Text>Quantity: {item.qty}</Text>
                          </View>
                        ))}
                      </View>
                    </View>
                  </View>
                ))}
              </View>
            </Page>
          );
        })}
      </Document>
    );
  };
  

  return (
    <div>
      <h1 style={{ textAlign: "center" }}>Packing List</h1>
      {/* Display orders with associated items */}
      {Object.keys(groupedOrders).map((portalOrderNo, index) => (
        <label key={index} style={{ display: 'block', backgroundColor: 'yellow', marginBottom: '20px', padding: '10px' }}>
          <div>
            <h3>Portal Order No: {portalOrderNo}</h3>
            {groupedOrders[portalOrderNo].map(order => (
              <div key={order.orderId}>
                <p>Portal: {order.portal}</p>
                <p>Ship by Date: {order.shipByDate}</p>
                <h4>Items:</h4>
                <ul>
                  {order.items.map(item => (
                    <li key={item.itemId}>
                      <p>SKU Code: {item.skucode}</p>
                      <p>Description: {item.description}</p>
                      {/* Quantity should be displayed for each item */}
                      <p>Quantity: {item.qty}</p> {/* Use item.qty instead of order.qty */}
                    </li>
                  ))}
                </ul>
              </div>
            ))}
          </div>
          <input
            type="checkbox"
            checked={selectedOrders.includes(portalOrderNo)}
            onChange={() => handleCheckboxChange(portalOrderNo)}
          />
          <span style={{ marginLeft: '5px' }}>Select Order</span>
        </label>
      ))}
      {!showSelectedOrders && (
        <div className="button-container">
          <button className="download-button" onClick={handlePrint}>Print Selected Orders</button>
          <PDFDownloadLink className="download-button" document={generatePDF()} fileName="picklist.pdf">
              {({ loading }) => (loading ? 'Loading...' : 'Download PDF')}
            </PDFDownloadLink>
        </div>
      )}
      {showSelectedOrders && (
        <>
          {pdfData}
          <div className='reset-buttons'>
            <button onClick={resetSelectedOrders}>Reset Selection</button>
            <PDFDownloadLink className="download-button" document={generatePDF()} fileName="picklist.pdf">
              {({ loading }) => (loading ? 'Loading...' : 'Download PDF')}
            </PDFDownloadLink>
          </div>
        </>
      )}
    </div>
  );
  
  
}

export default PackingList;
