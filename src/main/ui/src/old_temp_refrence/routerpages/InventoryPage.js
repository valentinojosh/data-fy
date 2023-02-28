import {Navbar} from "react-bootstrap";
import {useState} from "react";
import {Table} from "react-bootstrap";
import {Link} from "react-router-dom";

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
            <td>{props.id}</td>
            <td>{props.name}</td>
            <td>{props.count}</td>
            <td>
                {/*<Link to="/inventoryDetails" state={props.index}>Details</Link>*/}
                <Link to={`/inventoryDetails/${props.id}`}>Details</Link>
            </td>
        </tr>
    );
}

const InventoryPage = ({inventory}) => {
    const [sprocketChoice, setSprocketChoice] = useState("");

    return (
        <div>
            <Navbar.Brand style={titleStyle}>Inventory Page</Navbar.Brand>
            <div className="container-fluid">
                <div className="row">
                    <div className="col-md-8" id="col2">
                        <div>
                            <form>
                                <label>Choose Sprockets or Cogs:
                                    <select onChange={(e) => setSprocketChoice(e.target.value)}>
                                        <option key="none" value="None">None</option>
                                        <option key="sprockets" value="Sprocket">Sprockets</option>
                                        <option key="cogs" value="Cog">Cogs</option>
                                    </select>
                                </label>
                            </form>
                            <div style={resultsStyle}>Results:</div>

                            <div>
                                <Table responsive="sm">
                                    <tbody>
                                    <tr>
                                        <td>Item Id</td>
                                        <td>Name</td>
                                        <td>Count</td>
                                        <td>Details</td>
                                    </tr>
                                    {inventory.map((obj,index) => (obj.Category === sprocketChoice) && <Rows id={obj.Id} name={obj.Item} count={obj.Count} index={index}/>)}
                                    </tbody>
                                </Table>

                            </div>

                        </div>
                    </div>
                </div>
            </div>
        </div>
    )
};
export default InventoryPage;