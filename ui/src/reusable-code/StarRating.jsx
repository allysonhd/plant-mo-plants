import React, { useState, useEffect } from "react";
import { FaStar } from "react-icons/fa";
import { useParams } from "react-router-dom";

const StarRating = () => {
  const [rating, setRating] = useState(0);
  const [hover, setHover] = useState(null);
  const { plantId } = useParams();

  useEffect(() => {
    let ignore = false;
    async function fetchCurrentRating() {
      const token = localStorage.getItem("JWT_TOKEN");
      // const csrfToken = localStorage.getItem("CSRF_TOKEN");

      const response = await fetch(
        `http://localhost:8080/api/plant-rating/current-rating/${plantId}`,
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
      const currentRating = await response.json();

      if (!ignore) {
        setRating(currentRating.plantRating);
        console.log(currentRating);
      }
    }
    fetchCurrentRating();
    return () => {
      ignore = true;
    };
  }, [plantId]);

  //pass the ratingValue from the button click into the handle submit function
  async function handleSubmitStarRatingClick(ratingValue) {
    const dto = { plantRating: ratingValue, plantId: plantId };
    const token = localStorage.getItem("JWT_TOKEN");
    const csrfToken = localStorage.getItem("CSRF_TOKEN");

    try {
      const response = await fetch(
        `http://localhost:8080/api/plant-rating/create`,
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
        setRating(dtoData.plantRating);
      }
    } catch (error) {
      console.error("Error adding plant rating:", error);
    }
  }
  return (
    <div className="star-rating">
      {[...Array(5)].map((star, i) => {
        const ratingValue = i + 1;

        return (
          <label key={i}>
            <input
              type="radio"
              name="plantRating"
              value={ratingValue}
              onClick={() => {
                setRating(ratingValue);
                handleSubmitStarRatingClick(ratingValue);
              }}
            />
            <FaStar
              className="star"
              color={ratingValue <= (hover || rating) ? "#BA68C8" : "#D7CCC8"}
              size={20}
              onMouseEnter={() => setHover(ratingValue)}
              onMouseLeave={() => setHover(null)}
            ></FaStar>
          </label>
        );
      })}
    </div>
  );
};

export default StarRating;
