import { useNavigate, useParams } from "react-router-dom";
import React, { useEffect, useState, useContext } from "react";
import PlantCard from "../reusable-code/PlantCard.jsx";
import "../custom-css/PlantCard.css";
import "../custom-css/GardenDetail.css";
import "../custom-css/GardenConditions.css";
import GardenPhoto from "../reusable-code/GardenPhoto.jsx";
import { GardenContext } from "../store/GardenContext.jsx";
import {
  lightOptions,
  waterOptions,
  soilOptions,
} from "../reusable-code/gardenConditionsSelect";
import html2pdf from "html2pdf.js";
import { useRef } from "react";

function GardenDetails() {
  const { gardenId } = useParams();
  const [garden, setGarden] = useState(null);
  const [editMode, setEditMode] = useState(false);
  const [newName, setNewName] = useState("");
  const [refresh, setRefresh] = useState("");
  const navigate = useNavigate();
  const token = localStorage.getItem("JWT_TOKEN");
  const csrfToken = localStorage.getItem("CSRF_TOKEN");
  const [currentUser, setCurrentUser] = useState(null);
  const { gardenData } = useContext(GardenContext);
  const printRef = useRef();

  // Utility to match selected value with image/label
  const getConditionDetails = (options, value) =>
    options.find((option) => option.value === value);

  useEffect(() => {
    fetch("http://localhost:8080/api/garden/user", {
      headers: {
        Authorization: `Bearer ${token}`,
        "X-XSRF-TOKEN": csrfToken,
        Accept: "application/json",
      },
      credentials: "include",
    })
      .then((res) => res.json())
      .then((data) => setCurrentUser(data))
      .catch((err) => console.error("Error fetching user:", err));
  }, []);

  useEffect(() => {
    fetch(`http://localhost:8080/api/garden/garden-details/${gardenId}`, {
      headers: {
        Authorization: `Bearer ${token}`,
        "X-XSRF-TOKEN": csrfToken,
        Accept: "application/json",
      },
      credentials: "include",
    })
      .then((response) => response.json())
      .then((data) => {
        setGarden(data);
        setNewName(data.name);
      })
      .catch((err) => console.error("Error fetching garden details:", err));
  }, [gardenId, refresh]);


  const handleNameSave = () => {
    const confirm = window.confirm(
      "Are you sure you want to rename this garden?"
    );
    if (!confirm) return;

    const updatedGardenDTO = {
      gardenName: newName,
      gardenZone: garden.gardenZone,
      gardenLight: garden.gardenLight,
      gardenWater: garden.gardenWater,
      gardenSoil: garden.gardenSoil,
    };

    fetch(`http://localhost:8080/api/garden/garden-details/${gardenId}`, {
      method: "PUT",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${token}`,
        "X-XSRF-TOKEN": csrfToken,
        Accept: "application/json",
      },
      credentials: "include",
      body: JSON.stringify(updatedGardenDTO),
    })
      .then((response) => {
        if (!response.ok) {
          throw new Error(`Error: ${response.status}`);
        }
        return response.json();
      })
      .then((updated) => {
        setGarden(updated);
        setEditMode(false);
      })
      .catch((err) => console.error("Update failed:", err));
  };

  const handleDelete = () => {
    const confirm = window.confirm(
      "Are you sure you want to delete this garden?"
    );
    if (!confirm) {
      console.log("User canceled delete.");
      return;
    }

    console.log("User confirmed delete.");

    fetch(`http://localhost:8080/api/garden/delete/${gardenId}`, {
      method: "DELETE",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${token}`,
        "X-XSRF-TOKEN": csrfToken,
        Accept: "application/json",
      },
      credentials: "include",
    })
      .then((response) => {
        if (response.status === 204) {
          alert("Garden deleted successfully.");
          navigate("/dashboard");
        } else if (response.status === 404) {
          alert("Garden not found.");
        } else {
          throw new Error(`Unexpected status: ${response.status}`);
        }
      })
      .catch((err) => {
        console.error("Delete failed:", err);
        alert("An error occurred while deleting the garden.");
      });
  };

  const handlePrint = () => {
    const element = printRef.current;
    const opt = {
      margin: 0.5,
      filename: `${garden.gardenName}_Garden_Plan.pdf`,
      image: { type: "jpeg", quality: 0.98 },
      html2canvas: { scale: 2 },
      jsPDF: { unit: "in", format: "letter", orientation: "portrait" },
    };

    html2pdf().set(opt).from(element).save();
  };

  if (!garden) return <div>Loading garden...</div>;

  // Get matching condition data (label + img) from shared arrays
  const light = getConditionDetails(lightOptions, garden.gardenLight);
  const water = getConditionDetails(waterOptions, garden.gardenWater);
  const soil = getConditionDetails(soilOptions, garden.gardenSoil);

  return (
    <>
      {/* Hidden PDF Layout for printing */}
      <div style={{ display: "none" }}>
        <div className="print-pdf-container" ref={printRef}>
          {/* Logo */}
          <div style={{ textAlign: "center", marginBottom: "30px" }}>
            <img
              src="/images/plant-mo-plants-logo.png"
              alt="Plant MO Plants! Logo"
              style={{ width: "300px", height: "auto" }}
            />
          </div>

          <h1 className="garden-name">{garden.gardenName}</h1>

          <div style={{ marginBottom: "30px" }}>
            <h2 style={{ color: "#2c7a7b" }}>Garden Conditions</h2>
            <ul style={{ listStyle: "none", paddingLeft: 0 }}>
              <li>
                <strong>Zone:</strong> {garden.gardenZone}
              </li>
              <li>
                <strong>Light:</strong> {light?.label}
              </li>
              <li>
                <strong>Water:</strong> {water?.label}
              </li>
              <li>
                <strong>Soil:</strong> {soil?.label}
              </li>
            </ul>
          </div>

          <div>
            <h2 style={{ color: "#2c7a7b", marginBottom: "10px" }}>
              Plants in this Garden
            </h2>
            {garden.plants && garden.plants.length > 0 ? (
              Object.entries(
                garden.plants.reduce((grouped, plant) => {
                  const type = plant.plantType || "Unknown Type";
                  if (!grouped[type]) grouped[type] = [];
                  grouped[type].push(plant);
                  return grouped;
                }, {})
              ).map(([type, plants]) => (
                <div key={type} style={{ marginBottom: "2rem" }}>
                  <h3
                    style={{
                      borderBottom: "1px solid #ddd",
                      paddingBottom: "5px",
                      fontSize: "1rem",
                      color: "#3E2723",
                    }}
                  >
                    {type}
                  </h3>
                  <ul style={{ listStyle: "none", paddingLeft: 0, margin: 0 }}>
                    {plants
                      .sort((a, b) =>
                        (a.commonName || "").localeCompare(b.commonName || "")
                      )
                      .map((plant) => (
                        <li
                          key={plant.id}
                          style={{
                            display: "flex",
                            alignItems: "center",
                            gap: "1rem",
                            marginBottom: "1.5rem",
                          }}
                        >
                          <div>
                            <strong style={{ fontSize: "0.95rem" }}>
                              {plant.commonName || plant.scientificName}
                            </strong>
                          </div>
                          {plant.imageUrl && (
                            <img
                              src={plant.imageUrl}
                              alt={plant.commonName}
                              style={{
                                width: "100px",
                                height: "auto",
                                borderRadius: "5px",
                                boxShadow: "0 2px 6px rgba(0,0,0,0.1)",
                              }}
                            />
                          )}
                        </li>
                      ))}
                  </ul>
                </div>
              ))
            ) : (
              <p>No plants in this garden yet.</p>
            )}
          </div>
        </div>
      </div>

      {/* Visible UI */}
      <div className="garden-details-wrapper">
        <div className="garden-details-container">
          <div className="top-bar">
            <div className="left">
              {editMode ? (
                <div className="edit-name">
                  <input
                    value={newName}
                    onChange={(e) => setNewName(e.target.value)}
                  />
                  <button onClick={handleNameSave}>SAVE</button>
                  <span
                    className="edit-icon"
                    onClick={() => setEditMode(false)}
                  >
                    CANCEL
                  </span>
                </div>
              ) : (
                <h1 className="garden-name">
                  {garden.gardenName}{" "}
                  <span className="edit-icon" onClick={() => setEditMode(true)}>
                    ✏️
                  </span>
                </h1>
              )}
            </div>
            <div className="right">
              <button className="garden-button" onClick={handlePrint}>
                PRINT GARDEN PLAN
              </button>
              {/* <button
                className="garden-button" // You might want to adjust the className
                onClick={() => navigate("/photo-upload")}
              >
                Upload a Photo of your Garden!
              </button> */}

              <a href="/dashboard">
                <button className="nursery-button">FIND A NURSERY 🌱</button>
              </a>
            </div>
          </div>

          <div className="main-content">
            <GardenPhoto gardenId={gardenId} />{" "}
            <div className="conditions-box">
              <p className="condition-item">
                <strong>Zone:</strong> {garden.gardenZone}
              </p>

              {light && (
                <div className="condition-item">
                  <img
                    src={light.img}
                    alt={light.label}
                    className="condition-image"
                  />
                  <p>{light.label}</p>
                </div>
              )}

              {water && (
                <div className="condition-item">
                  <img
                    src={water.img}
                    alt={water.label}
                    className="condition-image"
                  />
                  <p>{water.label}</p>
                </div>
              )}

              {soil && (
                <div className="condition-item">
                  <img
                    src={soil.img}
                    alt={soil.label}
                    className="condition-image"
                  />
                  <p>{soil.label}</p>
                </div>
              )}
            </div>
          </div>

          <div className="plants-section">
            <div className="plants-header">
              <h1 className="garden-name">Plants in this Garden:</h1>
              <div className="right">
                <a href={`/plant-search/${garden.id}`}>
                  <button className="garden-button">ADD MORE PLANTS</button>
                </a>
                <button className="delete-button" onClick={handleDelete}>
                  DELETE GARDEN
                </button>
              </div>
            </div>
            {garden.plants && garden.plants.length > 0 ? (
              <div className="plant-list">
                {garden.plants.map((plant) => (
                  <PlantCard key={plant.id} plant={plant} gardenId={gardenId} setRefresh={setRefresh} />
                ))}
              </div>
            ) : (
              <p>No plants yet.</p>
            )}
          </div>
        </div>
      </div>
    </>
  );
}

export default GardenDetails;
