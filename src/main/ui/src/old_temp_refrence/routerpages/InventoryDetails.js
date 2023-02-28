import {Button, Navbar} from "react-bootstrap";
import {useState} from "react";
import {Table} from "react-bootstrap";
import {Link, useParams} from "react-router-dom";

var firstLoad = false;
const titleStyle = {
    fontSize: "40px",
    color: "blue",
}
const resultsStyle = {
    fontSize: "30px",
    color: "blue"
}

function Rows(props){
    return (
        <tr>
            <td>{props.category}</td>
            <td>{props.id}</td>
            <td>{props.name}</td>
            <td>{props.description}</td>
            <td>{props.count}</td>
            <td>{props.cost}</td>
        </tr>
    );
}

const InventoryPage = ({inventory}) => {
    const {id} = useParams();

    return (
        <div>
            <Navbar.Brand style={titleStyle}>Inventory Details of {id}</Navbar.Brand>
            <div className="container-fluid">
                <div className="row">
                    <div className="col-md-8" id="col2">
                        <div>
                            <div style={resultsStyle}>Results:</div>

                            <div>
                                <Table responsive="sm">
                                    <tbody>
                                    <tr>
                                        <td>Category</td>
                                        <td>Item Id</td>
                                        <td>Name</td>
                                        <td>Description</td>
                                        <td>Count</td>
                                        <td>Cost</td>
                                    </tr>
                                    {inventory.map((obj) => (obj.Id === id) && <Rows id={obj.Id} name={obj.Item} count={obj.Count} category={obj.Category} cost={obj.Cost} description={obj.Description}/>)}
                                    </tbody>
                                </Table>
                                <Link to="/inventory">
                                    <Button>
                                        <p>Back</p>
                                    </Button>
                                </Link>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    )
};
export default InventoryPage;