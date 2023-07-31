import React from "react";
import ReactApexChart from "react-apexcharts";

export default function Genres({data}) {
    const series = Object.values(data);
    const labels = Object.keys(data);

    const state = {
        series: series,
        options: {
            chart: {
                type: 'polarArea',
                height: '100%',
                width: '100%'
            },
            labels: labels,
            colors:['#9b59b6', '#2980b9', '#27ae60', '#f39c12', '#c0392b', '#7f8c8d'],
            stroke: {
                colors: ['#fff']
            },
            fill: {
                opacity: 0.8
            },
            legend: {
                position: 'bottom'
            },
            responsive: [{
                breakpoint: 480,
                options: {
                    chart: {
                        width: '100%'
                    }
                }
            }]
        }
    };

    return (
        <div id="chart">
            <ReactApexChart options={state.options} series={state.series} type="polarArea" />
        </div>
    );
}
