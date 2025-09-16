package com.example.spring_boot;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UploadProfilePictureTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void uploadProfilePicture_shouldReturn200() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
            "file",                       // <-- must match @RequestParam("file")
            "avatar.png",
            MediaType.IMAGE_PNG_VALUE,
            "fake-image".getBytes()
        );

        mockMvc.perform(
                multipart("/api/users/testuser/upload-profile-picture") // adjust path
                    .file(file)
        )
        .andExpect(status().isOk())
        .andExpect(content().string("Profile picture uploaded successfully!"));
    }
}
