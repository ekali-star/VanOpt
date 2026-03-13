package org.example.vanopt.algorithm;

import org.example.vanopt.dto.ShipmentDTO;

import java.util.ArrayList;
import java.util.List;

public class KnapsackSolver {
    public static List<ShipmentDTO> solve(int maxVolume, List<ShipmentDTO> shipments) {
        int n = shipments.size();

        int[][] dp = new int[n + 1][maxVolume + 1];

        for (int i = 1; i <= n; i++) {
            ShipmentDTO s = shipments.get(i - 1);
            for (int w = 0; w <= maxVolume; w++) {
                if (s.getVolume() <= w) {
                    dp[i][w] = Math.max(dp[i - 1][w], dp[i - 1][w - s.getVolume()] + s.getRevenue());
                } else {
                    dp[i][w] = dp[i - 1][w];
                }
            }
        }

        List<ShipmentDTO> selected = new ArrayList<>();
        int w = maxVolume;
        for (int i = n; i > 0; i--) {
            if (dp[i][w] != dp[i - 1][w]) {
                ShipmentDTO s = shipments.get(i - 1);
                selected.add(0, s);
                w -= s.getVolume();
            }
        }

        return selected;
    }
}
