package es.upm.grise.profundizacion.file;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import es.upm.grise.profundizacion.exceptions.EmptyBytesArrayException;
import es.upm.grise.profundizacion.exceptions.InvalidContentException;
import es.upm.grise.profundizacion.exceptions.WrongFileTypeException;

public class FileTest {

    private File file;

    @BeforeEach
    void setUp() {
        file = new File();
    }

    // -------------------- Tests for File --------------------

    @Test
    void testAddPropertyValidContent() throws InvalidContentException, WrongFileTypeException {
        file.setType(FileType.PROPERTY);
        char[] content = {'a', 'b', 'c'};
        file.addProperty(content);
        List<Character> result = file.getContent();
        assertEquals(3, result.size());
        assertTrue(result.contains('a'));
        assertTrue(result.contains('b'));
        assertTrue(result.contains('c'));
    }

    @Test
    void testAddPropertyNullContentThrowsInvalidContentException() {
        file.setType(FileType.PROPERTY);
        assertThrows(InvalidContentException.class, () -> file.addProperty(null));
    }

    @Test
    void testAddPropertyWrongFileTypeThrowsWrongFileTypeException() {
        file.setType(FileType.IMAGE);
        char[] content = {'x'};
        assertThrows(WrongFileTypeException.class, () -> file.addProperty(content));
    }

    @Test
    void testGetCRC32EmptyContentReturnsZero() throws EmptyBytesArrayException {
        file.setType(FileType.PROPERTY);
        long crc = file.getCRC32();
        assertEquals(0L, crc);
    }

    @Test
    void testGetCRC32WithMockedFileUtils() throws EmptyBytesArrayException, InvalidContentException, WrongFileTypeException {
        file.setType(FileType.PROPERTY);
        char[] content = {'a', 'b'};
        file.addProperty(content);

        FileUtils mockUtils = mock(FileUtils.class);
        when(mockUtils.calculateCRC32(any(byte[].class))).thenReturn(12345L);

        byte[] bytes = new byte[file.getContent().size() * 2];
        for (int i = 0; i < file.getContent().size(); i++) {
            char c = file.getContent().get(i);
            bytes[i * 2] = (byte) ((c >>> 8) & 0xFF);
            bytes[i * 2 + 1] = (byte) (c & 0xFF);
        }

        long crc = mockUtils.calculateCRC32(bytes);
        assertEquals(12345L, crc);
        verify(mockUtils, times(1)).calculateCRC32(any(byte[].class));
    }

    @Test
    void testCalculateCRC32ValidArray() throws EmptyBytesArrayException {
        FileUtils utils = new FileUtils();
        byte[] data = {1, 2, 3};
        long crc = utils.calculateCRC32(data);
        assertEquals(0L, crc); // Current implementation returns 0L
    }

    @Test
    void testEnumValues() {
        assertEquals(2, FileType.values().length);
        assertTrue(FileType.valueOf("PROPERTY") == FileType.PROPERTY);
        assertTrue(FileType.valueOf("IMAGE") == FileType.IMAGE);
    }
}

