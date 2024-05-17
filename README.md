# Spiderly - Search Engine

Spiderly is a crawler-based search engine designed to index and retrieve web content efficiently. This project demonstrates the implementation of efficient search algorithms, data structures, and a user-friendly interface for displaying search results.

## Features

- **Crawler-Based Indexing:** Efficiently crawls web pages to build a searchable index.
- **Search Algorithms:** Implements algorithms to rank and retrieve relevant search results.
- **User Interface:** Provides a clean and intuitive interface for displaying search results.
- **Scalability:** Designed to handle a growing number of users and search queries.

## Technologies Used

- **Programming Languages:** Java, JavaScript
- **Frameworks and Libraries:** Spring Boot, React
- **Data Storage:** MongoDB
- **Tools:** Git

## Installation

1. **Clone the Repository:**
   ```bash
   git clone https://github.com/WebDevSsquad/Spiderly.git
   cd Spiderly
   ```

2. **Set Up the Backend:**
   - Install **MongoDB** on your device.
   - Connect to `localhost:27017`
   - Install **java 21** on your device.
   - Run **CrawlerSystem** in `JavaServer\src\main\java\Crawler`.
  
3. **Set Up the Frontend**
   ```bash
   cd client
   npm install
   ```

4. **Run the Backend Server:**
   - Run **JavaServerApplication** in `JavaServer\src\main\java\com\Spiderly\JavaServer`.

5. **Run the Frontend:**
   ```bash
   cd client
   npm start
   ```

## Usage
- Open your web browser and navigate to `http://localhost:3000`.  
  
  ![alt text](assets/main.png)  
  

- Enter a search query in the search bar and view the results.

  ![alt text](assets/result.png)  
- Click on search results to navigate to the indexed web pages.


## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.
