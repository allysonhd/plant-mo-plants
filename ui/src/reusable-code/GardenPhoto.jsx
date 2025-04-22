import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import "../custom-css/GardenPhoto.css";

const GardenPhoto = ({ gardenId }) => {
  const [photo, setPhoto] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const navigate = useNavigate();

  useEffect(() => {
    const fetchGardenPhoto = async () => {
      setLoading(true);
      setError(null);
      setPhoto(null);

      try {
        const token = localStorage.getItem("JWT_TOKEN");
        const csrfToken = localStorage.getItem("CSRF_TOKEN");

        const response = await fetch(
          `http://localhost:8080/api/photo/garden/${gardenId}/user`,
          {
            method: "GET",
            headers: {
              Authorization: `Bearer ${token}`,
              "X-XSRF-TOKEN": csrfToken,
              Accept: "application/json",
            },
            credentials: "include",
          }
        );

        if (response.ok) {
          const data = await response.json();
          if (data && data.length > 0) {
            setPhoto(data[0]);
          } else {
            // Handle the case where no photo is found for this garden
            setPhoto(null);
          }
        } else if (response.status === 404) {
          setPhoto(null);
        } else {
          setError("Failed to fetch garden photo.");
          console.error("Failed to fetch garden photo:", response);
        }
      } catch (err) {
        setError("Error fetching garden photo.");
        console.error("Error fetching garden photo:", err);
      } finally {
        setLoading(false);
      }
    };

    if (gardenId) {
      fetchGardenPhoto();
    }
  }, [gardenId]);

  if (loading) return <p>Loading garden photo...</p>;
  if (error) return <p>{error}</p>;

  const handleUploadClick = () => {
    navigate(`/photo-upload/${gardenId}`);
  };

  return (
    <>
      {photo ? (
        <img
          src={`data:image/jpeg;base64,${photo.photoImage}`}
          alt={photo.photoName}
          style={{
            width: "700px",
            height: "400px",
            objectFit: "cover",
            borderRadius: "8px",
          }}
        />
      ) : (
        <div
          className="placeholder-image-container"
          style={{
            position: "relative",
            width: "800px",
            height: "400px",
            borderRadius: "8px",
            overflow: "hidden",
            cursor: "pointer",
          }}
          onClick={handleUploadClick}
        >
          <img
            src="/images/upload-garden-pic.jpeg"
            alt="Upload Garden Photo"
            style={{
              width: "100%",
              height: "100%",
              objectFit: "contain",
              transition: "opacity 0.3s ease",
            }}
            className="placeholder-image"
          />
          <div
            className="upload-text-overlay"
            style={{
              position: "absolute",
              top: 0,
              left: 0,
              width: "100%",
              height: "100%",
              display: "flex",
              justifyContent: "center",
              alignItems: "center",
              color: "white",
              fontSize: "1.5em",
              fontWeight: "bold",
              backgroundColor: "rgba(0, 0, 0, 0)",
              opacity: 0,
              transition: "opacity 0.3s ease, background-color 0.3s ease",
              textAlign: "center",
            }}
          >
            Upload Photo
          </div>
        </div>
      )}
    </>
  );
};

export default GardenPhoto;
