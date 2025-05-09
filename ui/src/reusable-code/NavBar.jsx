import React, { useState } from "react";
import { IoMenu } from "react-icons/io5";
import { RxCross2 } from "react-icons/rx";
import { Link, useNavigate, useLocation } from "react-router-dom";
import {
  AppBar,
  Toolbar,
  Typography,
  Box,
  Button,
  Container,
} from "@mui/material";
import "@fontsource/atma/600.css"; // from https://fonts.google.com/specimen/Atma?categoryFilters=Feeling:%2FExpressive%2FPlayful
import SetTheme from "./SetTheme";
import { useMyContext } from "../store/ContextApi";

const NavBar = () => {
  const pathName = useLocation().pathname;
  const [headerToggle, setHeaderToggle] = useState(false);
  const navigate = useNavigate();
  const navigateHome = () => {
    navigate("/");
  };
  const navigateDashboard = () => {
    navigate("/dashboard");
  };

  const { token, setToken, setCurrentUser, isAdmin, setIsAdmin } =
    useMyContext();

  const handleLogout = () => {
    localStorage.removeItem("JWT_TOKEN"); // Updated to remove token from localStorage
    localStorage.removeItem("USER"); // Remove user details as well
    localStorage.removeItem("CSRF_TOKEN");
    localStorage.removeItem("IS_ADMIN");
    setToken(null);
    setCurrentUser(null);
    setIsAdmin(false);
    navigate("/login");
  };

  return (
    <AppBar
      position="fixed"
      sx={{
        backgroundColor: "#cce3de",
        left: 0,
        width: "100%",
        textAlign: "center",
        boxShadow: 3,
        mt: "auto",
      }}
    >
      <Toolbar>
        <Button
          onClick={navigateHome}
          sx={{
            cursor: "pointer",
            display: "inline-block",
            "&:hover": {
              animation: "pulse 1.5s infinite",
            },
            "@keyframes pulse": {
              "0%": {
                transform: "scale(1)",
              },
              "50%": {
                transform: "scale(1.05)",
              },
              "100%": {
                transform: "scale(1)",
              },
            },
          }}
        >
          <Typography
            variant="h6"
            sx={{
              fontFamily: "Atma, sans-serif",
              fontSize: "1.5rem",
              fontWeight: 600,
              color: "#131200",
            }}
          >
            🌱 Plant MO Plants 🌱
          </Typography>
        </Button>
        <Box sx={{ flexGrow: 1 }} />
        {/* <Box sx={{ display: "flex", alignItems: "center", gap: 2 }}>
          <Button className="login-button" onClick={handleLogout}>
            {isLoggedIn ? "Logout" : "Login"}
          </Button>
        </Box> */}
        {/* <Button
          onClick={navigateDashboard}
          sx={{ color: "purple", marginRight: "50px" }}
        >
          Dashboard
        </Button> */}
        <ul
          className={`lg:static  absolute left-0  top-16 w-full lg:w-fit lg:px-0 sm:px-10 px-4  lg:bg-transparent bg-headerColor   ${
            headerToggle
              ? "min-h-fit max-h-navbarHeight lg:py-0 py-4 shadow-md shadow-slate-700 lg:shadow-none"
              : "h-0 overflow-hidden "
          }  lg:h-auto transition-all duration-100 font-montserrat text-textColor flex lg:flex-row flex-col lg:gap-8 gap-2`}
        >
          {token && (
            <>
              <Link to="/dashboard">
                <li
                  className={` py-2 cursor-pointer  hover:text-slate-300 ${
                    pathName === "/dashboard" ? "font-semibold " : ""
                  } `}
                >
                  Dash Board
                </li>
              </Link>
              <Link to="/create-garden">
                <li
                  className={` py-2 cursor-pointer  hover:text-slate-300 ${
                    pathName === "/create-garden" ? "font-semibold " : ""
                  } `}
                >
                  Create Garden
                </li>
              </Link>
            </>
          )}
          {token ? (
            <>
              <Link to="/profile">
                <li
                  className={` py-2 cursor-pointer  hover:text-slate-300 ${
                    pathName === "/profile" ? "font-semibold " : ""
                  }`}
                >
                  Profile
                </li>
              </Link>{" "}
              {isAdmin && (
                <Link to="/admin/users">
                  <li
                    className={` py-2 cursor-pointer uppercase   hover:text-slate-300 ${
                      pathName.startsWith("/admin") ? "font-semibold " : ""
                    }`}
                  >
                    Admin
                  </li>
                </Link>
              )}
              <button
                onClick={handleLogout}
                className="w-24 text-center bg-btnColor font-semibold px-4 py-2 rounded-sm cursor-pointer hover:text-slate-300"
              >
                LogOut
              </button>
            </>
          ) : (
            <Link to="/signup">
              <li className="w-24 text-center bg-btnColor font-semibold px-4 py-2 rounded-sm cursor-pointer hover:text-slate-300">
                SignUp
              </li>
            </Link>
          )}
          <span></span>

          <li>
            <SetTheme />
          </li>
        </ul>
        <span
          onClick={() => setHeaderToggle(!headerToggle)}
          className="lg:hidden block cursor-pointer text-textColor  shadow-md hover:text-slate-400"
        >
          {headerToggle ? (
            <RxCross2 className=" text-2xl" />
          ) : (
            <IoMenu className=" text-2xl" />
          )}
        </span>
      </Toolbar>
    </AppBar>
  );
};

export default NavBar;
