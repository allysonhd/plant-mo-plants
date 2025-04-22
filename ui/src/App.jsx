import {
  BrowserRouter as Router,
  Route,
  Routes,
  Navigate,
} from "react-router-dom";
import React from "react";
import "./App.css";
import WeatherWidget from "./reusable-code/WeatherWidget";
import HomePage from "./pages/HomePage";
import Dashboard from "./pages/Dashboard";
import CreateGarden from "./pages/CreateGarden";
import GardenConditions from "./pages/GardenConditions";
import DashboardAdmin from "./pages/DashboardAdmin";
import GardenDetails from "./pages/GardenDetails";
import GardenZone from "./pages/GardenZone";
import GardenSuccess from "./pages/GardenSuccess";
import Comment from "./reusable-code/Comment";
import PhotoUpload from "./reusable-code/PhotoUpload";
import Login from "./auth/login";
import Footer from "./reusable-code/Footer";
import { Container, Box } from "@mui/material";
import NavBar from "./reusable-code/NavBar";
import NurseryList from "./reusable-code/NurseryList";
import NurserySearch from "./reusable-code/NurserySearch";
import PlantCard from "./reusable-code/PlantCard";
import PlantSearch from "./pages/PlantSearch";
import Signup from "./auth/SignUp";
import PlantDetails from "./pages/PlantDetails";
import StarRating from "./reusable-code/StarRating";
import OAuth2RedirectHandler from "./auth/Oauth2RedirectHandler";

import { GardenProvider } from "./store/GardenContext";

import AccessDenied from "./reusable-code/AccessDenied";
import Admin from "./AdminAccess/Admin";
import ProtectedRoute from "./AdminAccess/ProtectedRoute";

import ForgotPassword from "./auth/ForgotPassword";
import ResetPassword from "./auth/ResetPassword";
import UserProfile from "./auth/UserProfile";

function App() {
  return (
    <Router>
      <Container
        maxWidth="lg"
        disableGutters
        sx={{
          display: "flex",
          flexDirection: "column",
          minHeight: "100vh",
          alignItems: "center",
        }}
      >
        <NavBar />
        <Box
          sx={{
            width: "100%",
            paddingBottom: "80px",
            display: "flex",
            maxWidth: "1200px",
            margin: "0 auto",
            paddingTop: "5rem",
          }}
        >
          <GardenProvider>
            <Routes>
              <Route path="/" element={<HomePage />} />
              <Route path="/login" element={<Login />} />
              <Route path="/signup" element={<Signup />} />
              <Route path="/forgot-password" element={<ForgotPassword />} />
              <Route path="/reset-password" element={<ResetPassword />} />
              <Route
                path="/oauth2/redirect"
                element={<OAuth2RedirectHandler />}
              />
              <Route path="/dashboard" element={<Dashboard />} />
              <Route path="/admin-dashboard" element={<DashboardAdmin />} />
              <Route path="/create-garden" element={<CreateGarden />} />
              <Route path="/garden-zone" element={<GardenZone />} />
              <Route path="/garden-conditions" element={<GardenConditions />} />
              <Route
                path="/garden-success/:gardenId"
                element={<GardenSuccess />}
              />
              <Route path="/plant-search/:gardenId" element={<PlantSearch />} />
              <Route
                path="/garden-details/:gardenId"
                element={<GardenDetails />}
              />
              <Route path="/comment/:plantId" element={<Comment />} />
              <Route path="/photo-upload/:gardenId" element={<PhotoUpload />} />
              <Route path="/results" element={<NurseryList />} />
              <Route path="/nursery-search" element={<NurserySearch />} />
              <Route
                path="/plant-details/:plantId"
                element={<PlantDetails />}
              />
              <Route path="star-rating" element={<StarRating />} />
              <Route path="/access-denied" element={<AccessDenied />} />
              <Route
                path="/admin/*"
                element={
                  <ProtectedRoute adminPage={true}>
                    <Admin />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/create-garden"
                element={
                  <ProtectedRoute>
                    <CreateGarden />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/dashboard"
                element={
                  <ProtectedRoute>
                    <Dashboard />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/profile"
                element={
                  <ProtectedRoute>
                    <UserProfile />
                  </ProtectedRoute>
                }
              />
            </Routes>
          </GardenProvider>
        </Box>

        <Footer />
      </Container>
    </Router>
  );
}

export default App;
