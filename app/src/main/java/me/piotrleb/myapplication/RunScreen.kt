import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.maps.model.JointType
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Polyline
import me.piotrleb.myapplication.RunViewModel
import me.piotrleb.myapplication.ui.theme.*
import androidx.compose.ui.draw.blur
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.ui.platform.LocalContext


@Composable
fun RunScreen(viewModel: RunViewModel = viewModel()) {
    val distance by viewModel.distance.collectAsState()
    val pace by viewModel.pace.collectAsState()
    val isRunning by viewModel.isRunning.collectAsState()
    val pathPoints = viewModel.pathPoints
    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize().background(DeepBlack)) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            uiSettings = MapUiSettings(
                zoomControlsEnabled = false,
                myLocationButtonEnabled = false
            ),
            properties = MapProperties(isMyLocationEnabled = true)
        ) {
            Polyline(
                points = pathPoints,
                color = NeonGreen,
                width = 12f,
                jointType = JointType.ROUND
            )
        }

        // 2. OVERLAY - STATYSTYKI (Glassmorphism)
        Column(
            modifier = Modifier.fillMaxSize().padding(20.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Bar

            // Centralna Karta - Tylko najważniejsze dane, półprzezroczyste
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(32.dp))
                    .background(Color.Black.copy(alpha = 0.7f))
                    .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(32.dp))
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("DYSTANS", color = Color.Gray, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Text(
                    text = String.format("%.2f KM", distance / 1000),
                    color = NeonGreen,
                    fontSize = 64.sp,
                    fontWeight = FontWeight.Black
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    SmallStatVertical("TEMPO", String.format("%.2f", pace), "min/km")
                    SmallStatVertical("CZAS", "24:15", "min")
                }
            }

            // 3. PRZYCISK AKCJI
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                StartButtonLarge(isRunning) { viewModel.toggleRun(context) }
            }
        }
    }
}

@Composable
fun SmallStatVertical(label: String, value: String, unit: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, color = Color.Gray, fontSize = 12.sp)
        Text(value, color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text(unit, color = Color.Gray, fontSize = 10.sp)
    }
}

@Composable
fun StartButtonLarge(isRunning: Boolean, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(bottom = 24.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            // Neonowa poświata (Glow effect)
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .blur(25.dp)
                    .background(
                        color = if (isRunning) HotPink.copy(alpha = 0.5f) else NeonGreen.copy(alpha = 0.5f),
                        shape = CircleShape
                    )
            )

            // Główny przycisk
            Button(
                onClick = onClick,
                modifier = Modifier.size(84.dp),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isRunning) HotPink else NeonGreen
                ),
                contentPadding = PaddingValues(0.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
            ) {
                Icon(
                    imageVector = if (isRunning) Icons.Default.Stop else Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier.size(42.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = if (isRunning) "ZATRZYMAJ" else "START",
            color = if (isRunning) HotPink else NeonGreen,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 14.sp,
            letterSpacing = 2.sp
        )
    }
}
object MapStyles {
    val DARK_JSON = """
    [
      { "elementType": "geometry", "stylers": [{ "color": "#212121" }] },
      { "elementType": "labels.icon", "stylers": [{ "visibility": "off" }] },
      { "elementType": "labels.text.fill", "stylers": [{ "color": "#757575" }] },
      { "elementType": "labels.text.stroke", "stylers": [{ "color": "#212121" }] },
      { "featureType": "administrative", "elementType": "geometry", "stylers": [{ "color": "#757575" }] },
      { "featureType": "poi", "elementType": "geometry", "stylers": [{ "color": "#181818" }] },
      { "featureType": "road", "elementType": "geometry.fill", "stylers": [{ "color": "#2c2c2c" }] },
      { "featureType": "road", "elementType": "labels.text.fill", "stylers": [{ "color": "#8a8a8a" }] },
      { "featureType": "water", "elementType": "geometry", "stylers": [{ "color": "#000000" }] }
    ]
    """.trimIndent()
}