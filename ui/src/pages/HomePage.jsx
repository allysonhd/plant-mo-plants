import { colors } from "@mui/material";
import { useNavigate, Link } from "react-router-dom";
import "@fontsource/atma/600.css";
import "../custom-css/Homepage.css";

function HomePage() {
  const navigate = useNavigate();

  const handleNavigateLogin = () => {
    navigate("/login"); // Make sure this path matches your route for ExampleOne
  };
  const handleNavigateCreateAccount = () => {
    navigate("/signup"); // Make sure this path matches your route for ExampleOne
  };
  const handleNavigateAdmin = () => {
    navigate("/admin-dashboard"); // Make sure this path matches your route for ExampleOne
  };
  return (
    <div className="home">
      <h1>
        Welcome<span style={{ fontSize: "36px" }}>,</span> Gardener
      </h1>
      <h2>
        Thank you for investing in your local ecosystem. <br />
        Let's find some native plants for your Missouri garden!
      </h2>
      <button className="home-button" onClick={handleNavigateCreateAccount}>
        Sign Up
      </button>
      <br></br>
      <br></br>
      <h3>
        Already a Member?{" "}
        <span>
          <Link to="/login" className="home-text-link">
            Sign In
          </Link>
        </span>
      </h3>
      <h3>or</h3>
      <Link to="/admin-dashboard" className="home-text-link">
        Go to Admin Dashboard
      </Link>
    </div>
  );
}

export default HomePage;
