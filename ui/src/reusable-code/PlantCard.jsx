import React, { useState, useEffect } from "react";
import "../custom-css/PlantCard.css";
import { Link } from "react-router-dom";

//get plant and gardenId from PlantSearch and pass as props
function PlantCard({ plant, gardenId, setRefresh }) {
  const [inGarden, setInGarden] = useState(false);
  const plantId = plant.id;

  useEffect(() => {
    let ignore = false;
    async function fetchPlantInGardenStatus() {
      const token = localStorage.getItem("JWT_TOKEN");
      // const csrfToken = localStorage.getItem("CSRF_TOKEN");

      const response = await fetch(
        `http://localhost:8080/api/garden/${gardenId}/get-status/${plantId}`,
        {
          method: "GET",
          headers: {
            Authorization: `Bearer ${token}`,
            // "X-XSRF-TOKEN": csrfToken,
            "Content-Type": "application/json",
            Accept: "application/json",
          },
          credentials: "include",
        }
      );
      const currentStatus = await response.json();

      if (!ignore) {
        setInGarden(currentStatus.plantInGarden);
      }
    }
    fetchPlantInGardenStatus();
    return () => {
      ignore = true;
    };
  }, [gardenId, plantId]);

  //on ADD TO GARDEN button click, POST request at "/{gardenId}/add-plants/{plantId}"
  async function handleAddPlantToGardenClick() {
    const dto = { plant: plant, gardenId: gardenId };
    const token = localStorage.getItem("JWT_TOKEN");
    const csrfToken = localStorage.getItem("CSRF_TOKEN");

    try {
      const response = await fetch(
        `http://localhost:8080/api/garden/${gardenId}/add-plant/${plantId}`,
        {
          method: "POST",
          headers: {
            Authorization: `Bearer ${token}`,
            "X-XSRF-TOKEN": csrfToken,
            "Content-Type": "application/json",
            Accept: "application/json",
          },
          body: JSON.stringify(dto),
          credentials: "include",
        }
      );

      const dtoData = await response.json();
      if (response.ok) {
        setInGarden(dtoData.plantInGarden);
        if (setRefresh) {
          setRefresh((prev) => !prev);
        }
      }
    } catch (error) {
      console.error("Error adding plant:", error);
    }
  }

  //on  REMOVE PLANT button click, DELETE request at "api/garden/{gardenId}/remove-plant/{plantId}"
  async function handleRemovePlantFromGardenClick() {
    const dto = { plant: plant, gardenId: gardenId };
    const token = localStorage.getItem("JWT_TOKEN");
    const csrfToken = localStorage.getItem("CSRF_TOKEN");

    try {
      const response = await fetch(
        `http://localhost:8080/api/garden/${gardenId}/remove-plant/${plantId}`,
        {
          method: "DELETE",
          headers: {
            Authorization: `Bearer ${token}`,
            "X-XSRF-TOKEN": csrfToken,
            "Content-Type": "application/json",
            Accept: "application/json",
          },
          body: JSON.stringify(dto),
          credentials: "include",
        }
      );

      const dtoData = await response.json();
      if (response.ok) {
        setInGarden(dtoData.plantInGarden);
        if (setRefresh) {
          setRefresh((prev) => !prev);
        }
      }
    } catch (error) {
      console.error("Error removing plant:", error);
    }
  }

  return (
    <div className="plantCard">
      <div>
        <Link to={`/plant-details/${plant.id}`}>
          <img
            className="plantCard-image"
            src={`http://localhost:8080/${plant.plantImagePath}`}
            alt={`Picture of ${plant.commonName}`}
          />
          <h2 className="plantCard-title">{plant.commonName}</h2>
          <br></br>
        </Link>
      </div>
      <div>
        <button
          className="plantCard-button"
          onClick={handleAddPlantToGardenClick}
          disabled={inGarden === true}
        >
          {inGarden ? "PLANT ADDED!" : "ADD TO GARDEN"}
        </button>
      </div>
      <div>
        <button
          className="plantCard-secondary-button"
          disabled={inGarden === false}
          onClick={handleRemovePlantFromGardenClick}
        >
          {inGarden ? "REMOVE PLANT" : "REMOVED"}
        </button>
      </div>
    </div>
  );
}
export default PlantCard;
