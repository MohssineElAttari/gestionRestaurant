package ma.projet.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ma.projet.entity.Dessert;
import ma.projet.entity.Entree;
import ma.projet.service.DessertService;

@RestController
@RequestMapping("/api/dessert")
@CrossOrigin("*")
public class DessertController {
	@Autowired
	private DessertService dessertService;
	@Autowired
	private HttpServletRequest request;
	@Autowired
	private ServletContext context;

	@PostMapping("/add")
	public Dessert addDessert(@RequestParam("dessert") String repas, @RequestParam("file") MultipartFile file)
			throws JsonParseException, JsonMappingException, IOException {
		Dessert dessert = new ObjectMapper().readValue(repas, Dessert.class);
		boolean isExist = new File(context.getRealPath("/photos/")).exists();
		System.out.println("PATH: " + context.getRealPath("/photos/"));
		if (!isExist) {
			new File(context.getRealPath("/photos/")).mkdir();
		}
		System.out.println(context.getRealPath("/photos/"));

		String filename = file.getOriginalFilename();
		String modifiedFileName = FilenameUtils.getBaseName(filename) + "_" + System.currentTimeMillis() + "."
				+ FilenameUtils.getExtension(filename);
		System.out.println(modifiedFileName);
		System.out.println(file.getOriginalFilename());
		System.out.println((context.getRealPath("/photos/" + File.separator + modifiedFileName)));
		dessert.setPhoto(modifiedFileName);
		File serverfile = new File(context.getRealPath("/photos/" + File.separator + modifiedFileName));

		try {
			FileUtils.writeByteArrayToFile(serverfile, file.getBytes());
		} catch (Exception e) {
			// TODO: handle exception
		}
		return dessertService.create(dessert);
	}

	@PutMapping("/update")
	public void updateDessert(@RequestBody Dessert b) {
		dessertService.update(b);
	}

	@DeleteMapping("/delete/{id}")
	public void deleteDessert(@PathVariable Long id) {
		dessertService.delete(dessertService.findById(id));
	}

	@GetMapping
	public List<Dessert> getDessert() {
		String filesPath = context.getRealPath("/photos/");
		File fileFolder = new File(filesPath);
		System.out.println("fileFolder :" + fileFolder);

		if (fileFolder != null) {
			for (final Dessert f : dessertService.findAll()) {
				System.out.println("file :" + f);
				File file = new File(filesPath + f.getPhoto());
				if (!file.isDirectory()) {
					String encodeBase64 = null;
					try {
						String extension = FilenameUtils.getExtension(file.getName());
						System.out.println("extension :" + extension);
						FileInputStream fileInputStream = new FileInputStream(file);
						System.out.println("fileInputStream :" + fileInputStream);

						byte[] bytes = new byte[(int) file.length()];
						System.out.println("bytes :" + bytes);

						fileInputStream.read(bytes);
						System.out.println("fileInputStream :" + fileInputStream);
						encodeBase64 = Base64.getEncoder().encodeToString(bytes);
						String img = "data:image/" + extension + ";base64," + encodeBase64;
						f.setPhoto(img);
						;

						fileInputStream.close();

					} catch (Exception e) {
						// TODO: handle exception
					}
				}

			}
		}
		return dessertService.findAll();
	}

	@GetMapping("/{id}")
	public Dessert finById(@PathVariable Long id) {
		return dessertService.findById(id);
	}
//	public Dessert addDessert(@RequestPart double prixRepas, @RequestPart int durreCuisson,
//			@RequestPart MultipartFile img, @RequestPart String nom, @RequestPart String type, @RequestPart String info)
//			throws IOException {
//		Dessert Dessert = new Dessert();
//		String photo = img.getOriginalFilename();
//		Dessert.setPrix(prixRepas);
//		Dessert.setDurreCuisson(durreCuisson);
//		Dessert.setPhoto(photo);
//		Dessert.setNom(nom);
//		Dessert.setType(type);
//		Dessert.setInfo(info);
//		DessertService.create(Dessert);
//		File upl = new File("images/" + photo);
//		upl.createNewFile();
//		FileOutputStream fout = new FileOutputStream(upl);
//		fout.write(img.getBytes());
//		fout.close();
//		return Dessert;
//
//	}
}
