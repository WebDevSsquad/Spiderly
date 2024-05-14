import axios from "axios";

export const searchQuery = async (query) => {
  try {
    const url = `http://localhost:8080/search?q=${query}`;
    const response = await axios.get(url, {
      headers: {
        "Content-Type": "application/json",
      },
    });
    
    // Update state with the fetched data and time taken
    return response.data.documents;
  } catch (error) {
    console.error("Error fetching data:", error.response);
    return null;
    // Handle the error appropriately
  }
};