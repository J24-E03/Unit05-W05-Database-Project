#  Final Database Project: Full Database Design & Integration

##  Project Objective
This capstone project is designed to assess your understanding of database concepts covered in this unit. You will:

- Choose your own project idea
- Design and implement a database with realistic relationships and constraints
- Write and save essential SQL queries
- Document and optionally integrate your database into a Java CLI application

---

##  Project Requirements

### 🔹 Step 1: Project Design

1. **Choose a Real-World Project Idea**  
   Pick a realistic system or application to build a database for. Examples:
   - Library Management System  
   - Event Booking App  
   - Fitness Tracker  
   - Freelance Job Board  

2. **User Stories (REQUIRED)**  
   Write **at least 8 user stories** in a `.txt` file to help define your database's needs.  
   Example:  
   `As a customer, I want to see a list of all products in a specific category.`


   `As a vendor, I want to be able to list an item in the shop.`


---

### 🔹 Step 2: Database Implementation (PostgreSQL)

Your database must have:

####  Structure

- ✅ **At least 7 tables (Many-To-Many Junction table counts as a table)**
- ✅ **Proper primary keys** on all tables
- ✅ Use of **foreign keys** to represent relationships (one-to-many, many-to-many, etc.)
- ✅ **CHECK constraint** on at least one column (e.g., age > 0, quantity >= 0)
- ✅ Use of **DATE**, **TIME**, or **TIMESTAMP** in at least one table

####  Queries & Logic

- ✅ Write **at least 10 logical SQL queries** that:
  - Use **JOINs**
  - Perform realistic business logic
- ✅ Save these queries as **views** for easier future access

#### Custom Logic

- ✅ Write at least **one user-defined function** in PostgreSQL
- ✅ Add **at least one trigger** to automate logic (e.g., timestamp update, audit logging)

---

###  Bonus Features (Optional but Encouraged)

####  Bonus 1

- Implement a **pagination function** that accepts `LIMIT` and `OFFSET`
- Use a **CRON job or scheduled logic** involving `TIME` or `DATE` (e.g., auto-archiving old records)
  - Document this using SQL comments in your script
- Use **COALESCE** in at least one essential query (if applicable)
- Use the **IN operator** for one of your queries (especially when using OR conditions)


####  Bonus 2: Java CLI Application (JDBC)

- Build a **Java CLI application** to interact with your database
- Use **JDBC PostgreSQL driver** for connectivity
- Include all essential queries and interactions from your SQL project
- Use **HikariCP** for efficient connection pooling
- Do **not hardcode credentials** (use a `.properties` file or environment variables) as much as possible
- Test all queries and flows to ensure they are working properly

---

##  Submission Instructions

You must submit:

### ✅ Required Files

1. 📄 One comprehensive SQL script OR multiple SQL files:
   - Table creation
   - Insert sample data
   - Views
   - Functions
   - Triggers

2. 📄 `user_stories.txt` with at least 8 user stories  
3. Screenshot of your ERD (Entity Relational Diagram) for your databse either just generated by PGAdmin or by using a platform like [Lucid Chart](https://www.lucidchart.com/pages/examples/er-diagram-tool)
4. ✅ Optional (Bonus): A Java CLI application with:
   - Source code
   - README explaining how to run
   - SQL dependencies & config







