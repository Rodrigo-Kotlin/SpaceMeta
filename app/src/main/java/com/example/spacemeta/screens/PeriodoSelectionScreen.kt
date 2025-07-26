package com.example.spacemeta.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.spacemeta.ui.theme.DataStoreManager
import kotlinx.coroutines.launch

@Composable
fun PeriodoSelectionScreen(navController: NavController) {
    val periods = listOf("51", "52", "53", "54")
    val colors = listOf(Color(0xFF2196F3), Color(0xFF4CAF50), Color(0XFFFF9800), Color(0XFF9C27B0))
    val context = LocalContext.current
    val dataStore = remember { DataStoreManager(context) }
    val scope = rememberCoroutineScope()

    var showDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .padding(24.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Filled.RocketLaunch,
                contentDescription = "Foguete",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(120.dp)
            )
        }
        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "RUMO AO SPACE!",
            style = MaterialTheme.typography.displaySmall
        )
        Spacer(
            modifier = Modifier.height(32.dp)
        )
        Column {
            for (i in periods.indices step 2) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                )
                {
                    for (j in 0..1) {
                        val index = i + j
                        if (index < periods.size) {
                            Card(
                                modifier = Modifier
                                    .padding(8.dp)
                                    .weight(1f)
                                    .height(120.dp)
                                    .clickable {
                                        val periodoNumero = periods[index].takeLast(2)
                                        navController.navigate("controle/$periodoNumero")
                                    },
                                colors = CardDefaults.cardColors(
                                    containerColor = colors[index]
                                ),
                                elevation = CardDefaults.cardElevation(8.dp),
                                shape = MaterialTheme.shapes.medium
                            )
                            {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                )
                                {
                                    Text(
                                        text = periods[index],
                                        style = MaterialTheme.typography.displaySmall,
                                        color = Color.White
                                    )
                                }

                            }
                        }
                    }
                    Spacer(
                        modifier = Modifier.height(16.dp)
                    )
                }
            }

        }
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = { showDialog = true },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("LIMPAR TODO OS DADOS")
        }
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                confirmButton = {
                    TextButton(onClick = {
                        scope.launch {
                            dataStore.limparTudo()
                            showDialog = false
                        }
                    }) {
                        Text("Confirmar")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text("Cancelar")
                    }
                },
                title = { Text("Confirmar Limpeza") },
                text = { Text("Tem certeza que deseja apagar todos os dados de metas e realizados de todos os módulos?") }
            )
        }
    }
}