package net.sample.wordpress.service;

import net.sample.wordpress.entity.User;
import net.sample.wordpress.repository.UserRepository;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class ExcelService {

    private final UserRepository userRepository;

    public ExcelService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void importExcel(MultipartFile file) throws Exception {
        try (InputStream is = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(is)) {

            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();

            List<User> users = new ArrayList<>();

            int rowNumber = 0;
            while (rows.hasNext()) {
                Row currentRow = rows.next();

                // Skip header row
                if (rowNumber == 0) {
                    rowNumber++;
                    continue;
                }

                User user = new User();

                // Match columns: Name | Email | Password | Role
                String name = currentRow.getCell(0).getStringCellValue();
                String email = currentRow.getCell(1).getStringCellValue();
                String password = currentRow.getCell(2).getStringCellValue();


                user.setUsername(name);
                user.setEmail(email);
                user.setPassword(password);


                users.add(user);
            }

            userRepository.saveAll(users);
        }
    }
}
