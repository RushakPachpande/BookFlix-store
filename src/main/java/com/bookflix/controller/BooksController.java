
package com.bookflix.controller;

import java.io.InputStream;
import java.nio.file.*;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import com.bookflix.dto.BookDto;
import com.bookflix.models.Book;
import com.bookflix.repository.BooksRepository;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/books")
public class BooksController {

	@Autowired
	private BooksRepository repo;

	@GetMapping({ "", "/" })
	public String showProductsList(Model model) {
		List<Book> books = repo.findAll(Sort.by(Sort.Direction.DESC, "id"));
		model.addAttribute("books", books);
		return "books/index";
	}

	@GetMapping("/create")
	public String showCreatePage(Model model) {
		BookDto bookDto = new BookDto();
		model.addAttribute("bookDto", bookDto);
		return "books/CreateBook";
	}

	@PostMapping("/create")
	public String createBook(@Valid @ModelAttribute BookDto bookDto, BindingResult result) {

		if (bookDto.getImageFile().isEmpty()) {
			result.addError(new FieldError("bookDto", "imageFile", "The Image file is required"));
		}

		if (result.hasErrors()) { return "books/CreateBook"; }

		// Save the image file to public/added_books folder
		MultipartFile image = bookDto.getImageFile();
		String storageFileName = image.getOriginalFilename();

		try {
			String uploadDir = "public/added_books/";
			Path uploadPath = Paths.get(uploadDir);

			if (!Files.exists(uploadPath)) {
				Files.createDirectories(uploadPath);
			}

			try (InputStream inputStream = image.getInputStream()) {
				Files.copy(inputStream, Paths.get(uploadDir + storageFileName), StandardCopyOption.REPLACE_EXISTING);
			}

		}
		catch (Exception ex) {
			System.out.println("Exception:" + ex.getMessage());
		}

		Book book = new Book();
		book.setName(bookDto.getName());
		book.setAuthor(bookDto.getAuthor());
		book.setCategory(bookDto.getCategory());
		book.setPrice(bookDto.getPrice());
		book.setImage(storageFileName);

		repo.save(book);

		return "redirect:/books";
	}

}
