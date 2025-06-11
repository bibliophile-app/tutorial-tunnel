CREATE TABLE booklist_books (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  book_id VARCHAR(32) NOT NULL,
  booklist_id BIGINT NOT NULL,
  added_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  
  UNIQUE KEY uq_book_per_list (book_id, booklist_id),
  FOREIGN KEY (booklist_id) REFERENCES booklists(id) ON DELETE CASCADE
);