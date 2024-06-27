import React, { useState, useEffect } from "react";
import axios from "axios";
import { Container, Row, Col, Card, ListGroup } from "react-bootstrap";
import { Bar } from 'react-chartjs-2';
import { Chart, CategoryScale, LinearScale, BarElement, Title, Tooltip, Legend } from 'chart.js';

// Register the required components with Chart.js
Chart.register(CategoryScale, LinearScale, BarElement, Title, Tooltip, Legend);

function StockCount() {
    const [apiData, setApiData] = useState([]);

    useEffect(() => {
        axios.get('http://localhost:8080/stock-counts/all')
            .then(response => {
                setApiData(response.data);
            })
            .catch(error => console.error(error));
    }, []); // Empty dependency array to ensure the effect runs only once

    const colors = [
        'rgba(75, 192, 192, 0.6)',
        'rgba(255, 99, 132, 0.6)',
        'rgba(255, 206, 86, 0.6)',
        'rgba(54, 162, 235, 0.6)',
        'rgba(153, 102, 255, 0.6)',
        'rgba(255, 159, 64, 0.6)',
        'rgba(75, 192, 192, 1)',
        'rgba(255, 99, 132, 1)',
        'rgba(255, 206, 86, 1)',
        'rgba(54, 162, 235, 1)',
        'rgba(153, 102, 255, 1)',
        'rgba(255, 159, 64, 1)'
    ];

    const chartData = {
        labels: apiData.map(item => item.item.skucode),
        datasets: [
            {
                label: 'Stock Count',
                data: apiData.map(item => item.count),
                backgroundColor: apiData.map((_, index) => colors[index % colors.length]),
                borderColor: apiData.map((_, index) => colors[(index + 6) % colors.length]), // Use a different set of colors for borders
                borderWidth: 1,
            }
        ],
    };

    const chartOptions = {
        responsive: true,
        maintainAspectRatio: false,
        animation: {
            duration: 2000, // Animation duration in milliseconds
            easing: 'easeInOutBounce', // Animation easing function
        },
        scales: {
            x: {
                beginAtZero: true,
                ticks: {
                    font: {
                        size: 14, // Font size for x-axis labels
                    },
                },
            },
            y: {
                beginAtZero: true,
                ticks: {
                    font: {
                        size: 14, // Font size for y-axis labels
                    },
                },
            },
        },
        plugins: {
            legend: {
                labels: {
                    font: {
                        size: 16, // Font size for legend labels
                    },
                },
            },
            title: {
                display: true,
                text: 'Stock Count Overview',
                font: {
                    size: 24, // Font size for the title
                },
            },
        },
    };

    return (
        <Container fluid>
            <Row className="my-4">
                <Col>
                    <h1>Stock Count</h1>
                </Col>
            </Row>
            <Row>
                <Col md={6}>
                <table className="table table-striped">
                <thead>
                    <tr>
                        <th scope="col">SKU Code</th>
                        <th scope="col">Description</th>
                        <th scope="col">Seller SKU</th>
                        <th scope="col">Count</th>
                    </tr>
                </thead>
                <tbody>
                    {apiData.map(item => (
                        <tr key={item.stockCountId}>
                            <td>{item.item.skucode}</td>
                            <td>{item.item.description}</td>
                            <td>{item.item.sellerSKUCode}</td>
                            <td>{item.count}</td>
                        </tr>
                    ))}
                </tbody>
            </table>
                </Col>
                <Col md={6}>
                    <Card style={{ height: '100%' }}>
                        <Card.Header style={{ fontWeight: "bolder" }}>Stock Count Chart</Card.Header>
                        <Card.Body>
                            <div style={{ height: '500px' }}>
                                <Bar data={chartData} options={chartOptions} />
                            </div>
                        </Card.Body>
                    </Card>
                </Col>
            </Row>
        </Container>
    );
}

export default StockCount;
