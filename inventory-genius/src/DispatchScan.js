import React, { useState } from 'react';
import BarcodeScanner from './BarcodeScanner.js';
import QRScanner from './QRScanner.js';
import axios from 'axios'; // Import Axios for making HTTP requests
import { toast, ToastContainer } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import successSound from './store-scanner-beep-90395.mp3'; // Import the sound file

const DispatchScan = () => {
    const [barcode, setBarcode] = useState('');
    const [qrCode, setQRCode] = useState('');

    const successAudio = new Audio(successSound); // Create a new Audio object

    // Function to call the API with the scanned code
    const callApiWithCode = async (code) => {
        console.log('Calling API with code:', code);
        try {
            const response = await axios.put(`http://localhost:8080/orders/scan/dispatch?awb=${code}`);
            console.log('API Response:', response.data);
            toast.success('Scan successful, order status changed', {
                autoClose: 2000 // Close after 2 seconds
              });
              successAudio.play();
            // Handle the API response here
        } catch (error) {
            console.error('Error:', error);
            toast.error('Scan failed' + error.message);

        }
    };

    const handleBarCodeDetected = (code) => {
        console.log('Barcode detected:', code);
        setBarcode(code); // Update barcode state
        callApiWithCode(code); // Call the API with the barcode
    };

    const handleQRCodeDetected = (data) => {
        console.log('QR code detected:', data);
        setQRCode(data); // Update QR code state
        callApiWithCode(data); // Call the API with the QR code
    };

    return (
        <div>
            <ToastContainer position="top-right" />
            <div className='title'>
                <h1>Scan Dispatched Orders</h1>
            </div>
            <div>
                <h3>Barcode Scanner</h3>
                <BarcodeScanner onDetected={handleBarCodeDetected} />
                {barcode && <p>Detected Barcode: {barcode}</p>}
            </div>
            <div>
                <h3>QR Code Scanner</h3>
                <QRScanner style={{ width: "100px" }} onScan={handleQRCodeDetected} />
                {qrCode && <p>Detected QR Code: {qrCode}</p>}
            </div>
        </div>
    );
};

export default DispatchScan;
