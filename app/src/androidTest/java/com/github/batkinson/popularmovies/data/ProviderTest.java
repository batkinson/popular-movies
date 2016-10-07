package com.github.batkinson.popularmovies.data;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.ProviderTestCase2;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.github.batkinson.popularmovies.data.ProviderContract.AUTHORITY;
import static com.github.batkinson.popularmovies.data.ProviderContract.Favorite.COLUMN_MOVIE_ID;
import static com.github.batkinson.popularmovies.data.ProviderContract.Favorite.CONTENT_TYPE;
import static com.github.batkinson.popularmovies.data.ProviderContract.Favorite.ITEM_CONTENT_TYPE;
import static com.github.batkinson.popularmovies.data.ProviderContract.Favorite.URI;
import static com.github.batkinson.popularmovies.data.ProviderContract.Favorite.getItemUri;
import static java.util.Arrays.asList;

@RunWith(AndroidJUnit4.class)
public class ProviderTest extends ProviderTestCase2<Provider> {

    public ProviderTest() {
        super(Provider.class, AUTHORITY);
    }

    @Before
    public void setUp() throws Exception {
        setContext(InstrumentationRegistry.getTargetContext());
        super.setUp();
    }

    @Test
    public void getTypeFavorites() {
        assertEquals(CONTENT_TYPE, getProvider().getType(URI));
    }

    @Test
    public void getTypeFavoriteItem() {
        assertEquals(ITEM_CONTENT_TYPE, getProvider().getType(getItemUri(1)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void getTypeInvalidUrl() {
        getProvider().getType(Uri.parse("content://not-a-valid-url"));
    }

    @Test
    public void insertAndQueryItem() {
        ContentResolver cr = getMockContentResolver();
        ContentValues values = new ContentValues();
        String expectedMovieId = "1234";
        values.put(COLUMN_MOVIE_ID, expectedMovieId);
        Uri itemUri = cr.insert(URI, values);
        Cursor results = cr.query(itemUri, null, null, null, null);
        assertNotNull("results cursor should be non-null", results);
        assertTrue("results cursor should move to a result", results.moveToNext());
        assertEquals(expectedMovieId, results.getString(results.getColumnIndex(COLUMN_MOVIE_ID)));
    }

    @Test
    public void bulkInsertAndQueryAllItems() {
        String[] movieIds = {"1", "123", "91", "467"};
        int createdCount = generateFavorites(movieIds);
        assertEquals("all movies should have been created", movieIds.length, createdCount);
        assertAllItems(movieIds);
    }

    @Test
    public void insertAndUpdateItem() {

        String origMovieId = "1234", newMovieId = "1235";
        ContentResolver cr = getMockContentResolver();

        // Insert and validate
        ContentValues values = new ContentValues();
        values.put(COLUMN_MOVIE_ID, origMovieId);
        Uri itemUri = cr.insert(URI, values);
        Cursor results = cr.query(itemUri, null, null, null, null);
        assertNotNull("results cursor should be non-null", results);
        assertTrue("results cursor should move to a result", results.moveToNext());
        assertEquals("expected original movie id after insert",
                origMovieId, results.getString(results.getColumnIndex(COLUMN_MOVIE_ID)));

        // Update and validate
        values.clear();
        values.put(COLUMN_MOVIE_ID, newMovieId);
        int updateCount = cr.update(itemUri, values, null, null);
        assertEquals("should have updated single item", 1, updateCount);
        results = cr.query(itemUri, null, null, null, null);
        assertNotNull("results cursor should be non-null", results);
        assertTrue("results cursor should move to a result", results.moveToNext());
        assertEquals("expected new movie id after update",
                newMovieId, results.getString(results.getColumnIndex(COLUMN_MOVIE_ID)));
    }

    @Test
    public void insertAndUpdateAll() {

        String[] origIds = {"1", "2", "3", "4"};
        String newId = "6";

        ContentResolver cr = getMockContentResolver();

        // Insert and validate
        int createdCount = generateFavorites(origIds);
        assertEquals("all movies should have been created", origIds.length, createdCount);
        assertAllItems(origIds);

        // Update and validate
        ContentValues toUpdate = toContentValues(newId)[0];
        int updateCount = cr.update(URI, toUpdate, null, null);
        assertEquals("should have updated all items", origIds.length, updateCount);
        assertAllItems("6");
    }

    @Test
    public void insertAndDeleteItem() {

        String movieId = "5673";
        String[] itemsNotToDelete = {"1", "18"};
        ContentResolver cr = getMockContentResolver();

        // Insert and validate
        Uri itemUri = cr.insert(URI, toContentValues(movieId)[0]);
        assertAllItems(movieId);

        // Delete and validate (with additional items present)
        assertEquals("expected all additional items to be generated",
                itemsNotToDelete.length, generateFavorites(itemsNotToDelete));
        int deleteCount = cr.delete(itemUri, null, null);
        assertEquals("should have deleted a single item", 1, deleteCount);
        assertAllItems(itemsNotToDelete);
    }

    @Test
    public void insertAndDeleteAllItems() {

        String[] items = {"5673", "134", "3354", "2234"};
        ContentResolver cr = getMockContentResolver();

        // Insert and validate
        assertEquals("expected to generate all items", items.length, generateFavorites(items));
        assertAllItems(items);

        // Delete and validate (with additional items present)
        int deleteCount = cr.delete(URI, null, null);
        assertEquals("should have deleted all items", items.length, deleteCount);
    }


    private int generateFavorites(String... movieIds) {
        return getMockContentResolver().bulkInsert(URI, toContentValues(movieIds));
    }

    private ContentValues[] toContentValues(String... movieIds) {
        ContentValues[] templateArray = {};
        List<ContentValues> items = new ArrayList<>();
        for (String movieId : movieIds) {
            ContentValues movie = new ContentValues();
            movie.put(COLUMN_MOVIE_ID, movieId);
            items.add(movie);
        }
        return items.toArray(templateArray);
    }

    private void assertAllItems(String... movieIds) {
        ContentResolver cr = getMockContentResolver();
        Cursor results = cr.query(URI, null, null, null, null);
        assertNotNull("results cursor should be non-null", results);
        assertTrue("results cursor should move to a result", results.moveToNext());
        Set<String> queriedIds = new HashSet<>();
        do {
            queriedIds.add(results.getString(results.getColumnIndex(COLUMN_MOVIE_ID)));
        } while (results.moveToNext());
        assertEquals("queried ids should be the same", new HashSet<>(asList(movieIds)), queriedIds);
    }

    @After
    public void tearDown() throws Exception {
        super.tearDown();
    }
}
