import React from "react";
import ReactApexChart from "react-apexcharts";

export default function Wrapped({artists, tracks, minutes}) {

    const options = {
        chart: {
            type: 'radialBar',
            offsetY: -30,
            sparkline: {
                enabled: true
            }
        },
        plotOptions: {
            radialBar: {
                startAngle: -90,
                endAngle: 90,
                track: {
                    background: "#e7e7e7",
                    strokeWidth: '100%',
                    margin: 5, // margin is in pixels
                    dropShadow: {
                        enabled: true,
                        top: 2,
                        left: 0,
                        color: '#999',
                        opacity: 1,
                        blur: 2
                    }
                },
                dataLabels: {
                    name: {
                        show: false
                    },
                    value: {
                        offsetY: -2,
                        fontSize: '22px',
                        formatter: function () {
                            return minutes;
                        }
                    }
                }
            }
        },
        grid: {
            padding: {
                top: 0
            }
        },
        fill: {
            colors: ['#22C55E'],
            type: 'gradient',
            gradient: {
                shade: 'light',
                shadeIntensity: 0.4,
                inverseColors: false,
                opacityFrom: 1,
                opacityTo: 1,
                stops: [0, 50, 53, 91]
            },
        },
    };

    const series = [(minutes/109200)*100];

    return (
        <div>
            <table>
                <tbody>
                <tr style={{
                    borderTop: "1px solid black",
                    borderLeft: "1px solid black",
                    borderRight: "1px solid black",
                    borderBottom: "none",
                    backgroundColor: "lightgrey"

                }}>
                    <td colSpan="5" >Minutes Listened This Year (est)</td>
                </tr>

                <tr style={{
                    borderTop: "None",
                    borderLeft: "1px solid black",
                    borderRight: "1px solid black",
                    borderBottom: "1px solid black",
                    backgroundColor: "lightgrey"
                }}>
                    <td colSpan={5}><div id="chart">
                        <ReactApexChart options={options} series={series} type="radialBar" />
                    </div></td>
                </tr>

                <tr style={{
                    borderTop: "1px solid black",
                    borderLeft: "1px solid black",
                    borderRight: "1px solid black",
                    borderBottom: "none",
                    backgroundColor: "white"
                }}>
                    <td colSpan={5}>Top 5 Artists</td>
                </tr>

                <tr style={{
                    borderTop: "none",
                    borderLeft: "1px solid black",
                    borderRight: "1px solid black",
                    borderBottom: "1px solid black",
                    backgroundColor: "white"
                }}>
                    {artists.map((item, index) => (
                        <td className="imageCell2" key={item.id}>
                            {item.images && item.images[0] && (
                                <a target="_blank"  rel="noreferrer" href={item.externalUrls.externalUrls.spotify}><img src={item.images[0].url} alt={item.name}/></a>
                            )}
                        </td>
                    ))}
                </tr>

                {/* Fourth row */}
                <tr style={{
                    borderTop: "1px solid black",
                    borderLeft: "1px solid black",
                    borderRight: "1px solid black",
                    borderBottom: "none",
                    backgroundColor: "lightgrey"
                }}>
                    <td colSpan="5">Recommended Songs</td>
                </tr>

                <tr style={{
                    borderTop: "none",
                    borderLeft: "1px solid black",
                    borderRight: "1px solid black",
                    borderBottom: "1px solid black",
                    backgroundColor: "lightgrey"
                }}>
                    {tracks.map((item, index) => (
                        <td className="imageCell2" key={item.id}>
                            <a target="_blank" rel="noreferrer" href={item.externalUrls.externalUrls.spotify}><img src={item.album.images[0].url} alt={item.name}/></a>
                        </td>
                    ))}
                </tr>
                </tbody>
            </table>
        </div>
    );
}