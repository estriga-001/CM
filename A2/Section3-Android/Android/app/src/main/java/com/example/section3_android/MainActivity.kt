package com.example.section3_android

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.section3_android.adapter.ImageAdapter
import com.example.section3_android.model.ImageItem
import com.example.section3_android.viewmodel.MainViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton

/**
 * MainActivity - The main entry point of the app.
 * Sets up the RecyclerView, Toolbar, SwipeRefreshLayout, and FAB.
 * Observes LiveData from the ViewModel to update the UI.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel
    private lateinit var imageAdapter: ImageAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var fabRefresh: FloatingActionButton
    private lateinit var toolbar: Toolbar

    // Number of images to fetch per request
    private val IMAGE_COUNT: Int = 6

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Handle system bar insets for edge-to-edge display
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize views
        initViews()

        // Set up the Toolbar as the ActionBar
        setSupportActionBar(toolbar)

        // Set up RecyclerView
        setupRecyclerView()

        // Initialize ViewModel
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        // Observe LiveData from the ViewModel
        observeViewModel()

        // Set up refresh listeners
        setupRefreshListeners()

        // Initial data load
        viewModel.fetchDogImages(IMAGE_COUNT)
    }

    /**
     * Finds and initializes all view references from the layout.
     */
    private fun initViews() {
        toolbar = findViewById(R.id.toolbar)
        recyclerView = findViewById(R.id.recyclerViewImages)
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout)
        fabRefresh = findViewById(R.id.fabRefresh)
    }

    /**
     * Configures the RecyclerView with a LinearLayoutManager and the ImageAdapter.
     */
    private fun setupRecyclerView() {
        imageAdapter = ImageAdapter()
        val layoutManager: LinearLayoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = imageAdapter
    }

    /**
     * Observes LiveData from the ViewModel and updates the UI accordingly.
     */
    private fun observeViewModel() {
        // Observe the image list
        viewModel.imageList.observe(this, Observer<List<ImageItem>> { imageList ->
            if (imageList != null) {
                imageAdapter.setImageList(imageList)
            }
        })

        // Observe loading state
        viewModel.isLoading.observe(this, Observer<Boolean> { isLoading ->
            if (isLoading != null) {
                swipeRefreshLayout.isRefreshing = isLoading
            }
        })

        // Observe error messages
        viewModel.errorMessage.observe(this, Observer<String> { error ->
            if (error != null && error.isNotEmpty()) {
                Toast.makeText(this@MainActivity, error, Toast.LENGTH_SHORT).show()
            }
        })
    }

    /**
     * Sets up click/swipe listeners for refresh actions.
     */
    private fun setupRefreshListeners() {
        // SwipeRefreshLayout pull-to-refresh
        swipeRefreshLayout.setOnRefreshListener {
            viewModel.refreshImages(IMAGE_COUNT)
        }

        // FAB click to refresh
        fabRefresh.setOnClickListener {
            viewModel.refreshImages(IMAGE_COUNT)
        }
    }
}