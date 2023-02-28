import {BrowserRouter, Routes, Route} from "react-router-dom";
import Home from "./routerpages/Home";
import InventoryPage from "./routerpages/InventoryPage";
import InventoryDetails from "./routerpages/InventoryDetails";


const AppRouter = ({inventory}) => {
    return (
        <div className="App">
            <BrowserRouter>
                <Routes>
                    <Route path="/" element={<Home/>} />
                    <Route path="/inventory" element={<InventoryPage inventory={inventory}/>} />
                    <Route path="/inventoryDetails/:id" element={<InventoryDetails inventory={inventory}/>} />
                </Routes>
            </BrowserRouter>
        </div>
    );
}

export default AppRouter;