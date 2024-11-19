package com.example.smartbikehelmet.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import com.example.smartbikehelmet.R
import com.example.smartbikehelmet.databinding.FragmentDashboardBinding

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)

        // Initialize WebView
        val mapWebView = binding.mapWebView

        // Enable JavaScript
        val webSettings: WebSettings = mapWebView.settings
        webSettings.javaScriptEnabled = true

        // Set a WebViewClient to handle loading the HTML
        mapWebView.webViewClient = WebViewClient()

        // Load the local HTML file with Leaflet map

mapWebView.loadUrl("file:///android_asset/leaflet_map.html")
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Clear the binding reference
    }
}
