import React, { useState, useEffect } from 'react';
import BarcodeScanner from './BarcodeScanner.js';
import QRScanner from './QRScanner.js';
import axios from 'axios';
import { toast, ToastContainer } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import successSound from './store-scanner-beep-90395.mp3';
import "./Scan.css"; // Import your custom CSS file

const PackScan = () => {
    const [barcode, setBarcode] = useState('');
    const [qrCode, setQRCode] = useState('');
    const [orders, setOrders] = useState([]);
    const [selectedOrders, setSelectedOrders] = useState([]);

    const successAudio = new Audio(successSound);

    useEffect(() => {
        fetchOrders();
    }, []);

    // Function to fetch orders from the backend
    const fetchOrders = async () => {
        try {
            const response = await axios.get('http://localhost:8080/orders/notPacked');
            setOrders(response.data); // Assuming response.data is an array of orders
            console.log(orders);
        } catch (error) {
            console.error('Error fetching orders:', error);
            toast.error('Failed to fetch orders');
        }
    };

    // Function to call the API with the scanned code
    const callApiWithCode = async (code) => {
        console.log('Calling API with code:', code);
        try {
            const response = await axios.put(`http://localhost:8080/orders/scan/pack?awb=${code}`);
            console.log('API Response:', response.data);
            toast.success('Scan successful, order status changed', {
                autoClose: 2000
            });
            successAudio.play();
            fetchOrders(); // Refresh the list of orders after scanning
        } catch (error) {
            console.error('Error:', error);
            toast.error('Scan failed: ' + error.message);
        }
    };

    const handleBarCodeDetected = (code) => {
        console.log('Barcode detected:', code);
        setBarcode(code);
        callApiWithCode(code);
    };

    const handleQRCodeDetected = (data) => {
        console.log('QR code detected:', data);
        setQRCode(data);
        callApiWithCode(data);
    };

    // Toggle selection of an order
    const toggleOrderSelection = (orderId) => {
        const isSelected = selectedOrders.includes(orderId);
        if (isSelected) {
            setSelectedOrders(selectedOrders.filter(id => id !== orderId));
        } else {
            setSelectedOrders([...selectedOrders, orderId]);
        }
    };

    // Save selected orders
    const saveSelectedOrders = async () => {
        try {
            // Iterate through selectedOrders array
            for (const orderId of selectedOrders) {
                const order = orders.find(o => o.orderId === orderId); // Find the order details
                const response = await axios.put(`http://localhost:8080/orders/packByAwbNo?awbNo=${order.awbNo}`);
                console.log(`Packed order with AWB No. ${order.awbNo}:`, response.data);
            }
    
            toast.success('Selected orders packed successfully');
            fetchOrders(); // Refresh the list of orders after saving
        } catch (error) {
            console.error('Error packing orders:', error);
            toast.error('Failed to pack orders');
        }
    };

    return (
        <div className="pack-scan-container">
            <ToastContainer position="top-right" />
            <div className='title'>
                <h1>Scan Packed Orders</h1>
            </div>
            <div className="content-container">
                <div className="scanners">
                    <div className="scanner">
                        <h3>Barcode Scanner</h3>
                        <BarcodeScanner onDetected={handleBarCodeDetected} />
                        {barcode && <p>Detected Barcode: {barcode}</p>}
                    </div>
                    <div className="scanner">
                        <h3>QR Code Scanner</h3>
                        <QRScanner style={{ width: "100px" }} onScan={handleQRCodeDetected} />
                        {qrCode && <p>Detected QR Code: {qrCode}</p>}
                    </div>
                </div>
                <div className="orders-list">
                    <h3>Orders to Pack</h3>
                    {orders.map(order => (
                        <div key={order.orderId} className="order-item">
                            <input
                                type="checkbox"
                                checked={selectedOrders.includes(order.orderId)}
                                onChange={() => toggleOrderSelection(order.orderId)}
                            />
                            <label>{order.orderNo} ( {order.awbNo} )</label>
                        </div>
                    ))}
                    <button onClick={saveSelectedOrders}>Pack Selected Orders</button>
                </div>
            </div>
        </div>
    );
};

export default PackScan;
