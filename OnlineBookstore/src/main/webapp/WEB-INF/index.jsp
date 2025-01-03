<html>
<body>
<h2>Book Store</h2>
 <form action="registerBook" method="post">
        <!-- book title -->
        <label for="title">Book:</label>
        <input type="text" id="title" name="title" placeholder="Enter book title" required>

        <!-- book author -->
        <label for="status">book author:</label>
        <input type="text" id="author" name="author" placeholder="Enter book author" required>
        <!--  price --> -->
        <label for="price">Price:</label>
        <input type="text" id="price" name="price" required>

        <!-- Submit Button -->
        <button type="submit">Register Book</button>
    </form>
    <a href="displayBooks">View All Books</a>
    
    
<form action="deleteBook" method="post">
    <!-- Task ID -->
    <label for="id">Book ID:</label>
    <input type="number" id="id" name="id" placeholder="Enter book ID" required>

    <!-- Submit Button -->
    <button type="submit">Delete Book</button>
</form>
<form action="SearchBooks" method="get">
    <!-- Task Description -->
    <label for="description">Search by Title:</label>
    <input type="text" id="title" name="title" placeholder="Enter book title" required>

    <!-- Submit Button -->
    <button type="submit">Search</button>
</form>
</body>
</html>
