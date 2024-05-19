import React, { useState, useEffect } from "react";
import axios from "axios";

function StockCount() {
    const [apiData, setApiData] = useState([]);

    useEffect(() => {
        axios.get('http://localhost:8080/stock-counts/all')
            .then(response => {
                setApiData(response.data);
            })
            .catch(error => console.error(error));
    }, []); // Empty dependency array to ensure the effect runs only once

    return (
        <div>
            <h1>Stock Count</h1>
            <ul>
                {apiData.map(item => (
                    <li key={item.stockCountId}>
                        {item.item.skucode}({item.item.description}) - {item.count}
                        {/* Render other properties of the stock count as needed */}
                    </li>
                ))}
            </ul>
        </div>
    );
}

export default StockCount;
