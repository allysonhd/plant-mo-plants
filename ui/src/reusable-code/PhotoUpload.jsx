import { useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import "../custom-css/Dashboard.css";

const PhotoUpload = ({ userId }) => {
  const [file, setFile] = useState(null);
  const { gardenId } = useParams();
  const navigate = useNavigate();

  const handleFileChange = (e) => {
    setFile(e.target.files[0]);
  };

  const handleUpload = async () => {
    if (!file || !gardenId) {
      alert("Please select a file");
      return false;
    }

    const formData = new FormData();
    formData.append("file", file);

    try {
      const token = localStorage.getItem("JWT_TOKEN");
      const csrfToken = localStorage.getItem("CSRF_TOKEN");

      const response = await fetch(
        `http://localhost:8080/api/photo/upload/${gardenId}`,
        {
          method: "POST",
          headers: {
            Authorization: `Bearer ${token}`,
            "X-XSRF-TOKEN": csrfToken,
            Accept: "application/json",
          },
          credentials: "include",
          body: formData,
        }
      );

      if (response.ok) {
        const uploadedPhotoData = await response.json();
        setFile(null);
        return uploadedPhotoData.garden.id;
      } else {
        const errorData = await response.json();
        console.error("Upload failed:", errorData);
        alert("Failed to upload photo.");
      }
    } catch (error) {
      console.error("Error uploading photo:", error);
    }
  };

  return (
    <div className="dashboard-wrapper center">
      <div className="card photo-upload-card">
        <div className="upload-content">
          <h2 className="personalize-label">Upload a Garden Photo</h2>
          <p style={{ color: "#4F6F52", fontSize: "1rem" }}>
            Upload your garden 🌿
          </p>

          <form
            className="photo-form"
            onSubmit={(e) => {
              e.preventDefault();
              handleUpload().then((uploadedGardenId) => {
                if (uploadedGardenId) {
                  navigate(`/garden-details/${uploadedGardenId}`, {
                    state: { photoUploaded: true },
                  });
                }
              });
            }}
          >
            <label className="form-label">Select Photo:</label>
            <input
              type="file"
              onChange={handleFileChange}
              className="form-input"
              required
            />

            <div className="button-group">
              <button type="submit" className="dashboard-button">
                Upload
              </button>
              <button
                type="button"
                className="dashboard-button"
                onClick={() => navigate(`/garden-details/${gardenId}`)}
              >
                Cancel
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
};

export default PhotoUpload;
