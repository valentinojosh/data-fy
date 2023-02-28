import {Container, Nav, Navbar} from "react-bootstrap";
// import logo from './logo.png';

const NavBar = () => {
    return (
        <Navbar bg="light" expand="lg">
            <Container>
                {/*<Navbar.Brand href="#home"><img src={logo} alt="Logo" /></Navbar.Brand>*/}
                <Navbar.Toggle aria-controls="basic-navbar-nav" />
                <Navbar.Collapse id="basic-navbar-nav">
                    <Nav className="me-auto">
                        <Nav.Link href="/">Home</Nav.Link>
                        <Nav.Link href="/inventory">Inventory</Nav.Link>
                    </Nav>
                </Navbar.Collapse>
            </Container>
        </Navbar>
    )
};
export default NavBar;