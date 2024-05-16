package de.hsb.greenquest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.HorizontalAlignmentLine
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import de.hsb.greenquest.ui.theme.AppViewModelProvider
import de.hsb.greenquest.ui.theme.GreenQuestTheme
import androidx.lifecycle.viewmodel.compose.viewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GreenQuestTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize().background(Color(0xFFB69DF8)),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ChallengeScreen("Android")
                }
            }
        }
    }
}

@Composable
fun ChallengeScreen(name: String, modifier: Modifier = Modifier, viewModel: ChallengeViewModel = viewModel(factory = AppViewModelProvider.Factory)) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(Color(0xFFB69DF8))
    ) {
        Row(
            Modifier.weight(1f).fillMaxWidth().padding(horizontal = 40.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ){
            Image(
                modifier = Modifier,
                painter = painterResource(R.drawable.ic_launcher_foreground),
                contentDescription = "1"
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(Modifier.weight(1f).fillMaxWidth().padding(horizontal = 40.dp)){
            Column(modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceAround,
                horizontalAlignment = Alignment.CenterHorizontally
                ){

                ChallengeCard()
                ChallengeCard()
                ChallengeCard()
                ChallengeCard()
            }
        }
    }
}

@Composable
fun ChallengeCard(modifier: Modifier = Modifier) {
    Card(modifier = modifier.fillMaxWidth()){
        Row(modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween){
            Text(
                text = "Challenge 1",
                fontSize = 24.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(16.dp)
            )
            LinearProgressIndicator(
                progress = 0.5f,
                modifier = Modifier.fillMaxWidth(),
            )

        }

    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    GreenQuestTheme {
        ChallengeScreen("Android")
    }
}