package com.jadecook.proj6

import android.os.Bundle
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.codepath.asynchttpclient.AsyncHttpClient
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import okhttp3.Headers
import org.json.JSONException
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    private lateinit var rv: RecyclerView
    private lateinit var adapter: CharacterAdapter
    private lateinit var etQuery: TextInputEditText
    private lateinit var btnSearch: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        rv = findViewById(R.id.rvCharacters)
        etQuery = findViewById(R.id.etQuery)
        btnSearch = findViewById(R.id.btnSearch)

        adapter = CharacterAdapter(mutableListOf())
        rv.adapter = adapter

        val lm = LinearLayoutManager(this)
        rv.layoutManager = lm
        rv.addItemDecoration(DividerItemDecoration(this, lm.orientation))

        // Initial load
        fetchCharacters()

        // Search button
        btnSearch.setOnClickListener { performSearch() }

        // Keyboard action
        etQuery.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch(); true
            } else false
        }
    }

    private fun performSearch() {
        val q = etQuery.text?.toString()?.trim().orEmpty()
        fetchCharacters(q)
    }

    /**
     * Calls Rick & Morty API using AsyncHttpClient.
     * If nameQuery is non-empty, filters with ?name=<query>
     */
    private fun fetchCharacters(nameQuery: String = "", page: Int = 1) {
        val client = AsyncHttpClient()
        val base = "https://rickandmortyapi.com/api/character"
        val url = if (nameQuery.isBlank()) {
            "$base?page=$page"
        } else {
            "$base?page=$page&name=${nameQuery}"
        }

        client.get(url, object : JsonHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Headers, json: JSON) {
                try {
                    val root = json.jsonObject
                    val results = root.getJSONArray("results")
                    val parsed = mutableListOf<ApiCharacter>()

                    for (i in 0 until results.length()) {
                        val obj = results.getJSONObject(i)
                        val name = obj.getString("name")
                        val status = obj.getString("status")
                        val species = obj.getString("species")
                        val image = obj.getString("image")
                        val location: JSONObject = obj.getJSONObject("location")
                        val locationName = location.getString("name")

                        parsed.add(
                            ApiCharacter(
                                name = name,
                                status = status,
                                species = species,
                                imageUrl = image,
                                locationName = locationName
                            )
                        )
                    }

                    adapter.replaceAll(parsed)

                    if (nameQuery.isNotBlank()) {
                        Toast.makeText(
                            this@MainActivity,
                            "Found ${parsed.size} result(s) for \"$nameQuery\"",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                } catch (e: JSONException) {
                    Log.e("MainActivity", "JSON parse error", e)
                    Toast.makeText(this@MainActivity, "Parse error", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(
                statusCode: Int,
                headers: Headers?,
                response: String?,
                throwable: Throwable?
            ) {
                Log.e("MainActivity", "Request failed: $statusCode $response", throwable)
                val msg = when (statusCode) {
                    404 -> "No results. Try a different search."
                    else -> "Network error ($statusCode)"
                }
                Toast.makeText(this@MainActivity, msg, Toast.LENGTH_SHORT).show()
                if (statusCode == 404) adapter.replaceAll(emptyList())
            }
        })
    }
}
