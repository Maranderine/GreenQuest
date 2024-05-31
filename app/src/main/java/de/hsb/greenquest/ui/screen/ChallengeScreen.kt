package de.hsb.greenquest.ui.screen

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import de.hsb.greenquest.ui.viewmodel.ChallengeViewModel
import de.hsb.greenquest.R
import de.hsb.greenquest.data.local.entity.LocalChallengeEntity
import de.hsb.greenquest.data.repository.toExternal
import de.hsb.greenquest.domain.model.Challenge
import de.hsb.greenquest.ui.viewmodel.PortfolioViewModel
import kotlinx.coroutines.launch

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun ChallengeScreen(name: String, modifier: Modifier = Modifier, viewModel: ChallengeViewModel = hiltViewModel<ChallengeViewModel>()) {
    val coroutineScope = rememberCoroutineScope()
    val challenges = viewModel.challengeList.collectAsState().value


    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(Color(0xFFB69DF8))
    ) {
        Row(
            Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 40.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ){
            Image(
                modifier = Modifier.clickable { coroutineScope.launch {
                    viewModel?.insert()
                } },
                painter = painterResource(R.drawable.ic_launcher_foreground),
                contentDescription = "1"
            )
        }
        //Spacer(modifier = Modifier.height(16.dp))
        Row(
            Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 40.dp)
        ){

            if(challenges?.size == 0){
                Column(
                    modifier = Modifier.fillMaxWidth().fillMaxHeight(),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally

                ){
                    Image(
                        modifier = Modifier.clickable { coroutineScope.launch {
                            viewModel?.refreshChallenges()
                        } },
                        painter = painterResource(R.drawable.baseline_refresh_24),
                        contentDescription = "1"
                    )
                }
            }

            LazyColumn(modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
                //verticalArrangement = Arrangement.SpaceAround,
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                items(challenges?.size?: 0) { index ->
                    ChallengeCard(
                        modifier = Modifier.clickable { coroutineScope.launch {
                            if (viewModel != null) {
                                val challenge = challenges.get(index).toExternal()
                                viewModel.updateChallenge(challenge.copy(progress = challenge.progress+1))
                            }
                        } },
                        challenge = challenges.toExternal()[index]
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
                //item {
                //  Spacer(modifier = Modifier.height(16.dp))
                //}
            }
        }
    }
}

@Composable
fun ChallengeCard(modifier: Modifier = Modifier, challenge: Challenge) {
    Card(modifier = modifier.fillMaxWidth()){
        Row(modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween){
            Text(
                text = challenge.Plant,
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(16.dp)
            )
            if(!challenge.done){
                Box(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    contentAlignment = Alignment.Center
                    //   .background(color = Color.Yellow),
                ){
                    CircularProgressIndicator(
                        progress = challenge.progress.toFloat() / challenge.requiredCount.toFloat() ,
                        //modifier = Modifier.fillMaxWidth(),
                    )
                    Text(
                        text = challenge.progress.toString(),
                        fontSize = 24.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }else{
                Image(
                    painter = painterResource(R.drawable.baseline_check_24),
                    contentDescription = "1"
                )
            }
        }
    }
}

//TODO class for application broadcasts
//TODO HttpOK
//TODO datasource and repo for application wide events
//TODO datasource and repo for network calls