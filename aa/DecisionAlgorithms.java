import java.util.*;

public class DecisionAlgorithms {

    // ==========================================
    // 1. 廣度優先搜尋 (BFS)
    // ==========================================
    public static List<String> bfsShortestPath(Map<String, List<String>> graph, String start, String target) {
        if (start.equals(target)) return Arrays.asList(start);

        Set<String> visited = new HashSet<>();
        Queue<List<String>> queue = new LinkedList<>();
        queue.add(Arrays.asList(start));

        while (!queue.isEmpty()) {
            List<String> path = queue.poll();
            String node = path.get(path.size() - 1);

            if (!visited.contains(node)) {
                List<String> neighbors = graph.getOrDefault(node, Collections.emptyList());
                for (String neighbor : neighbors) {
                    List<String> newPath = new ArrayList<>(path);
                    newPath.add(neighbor);
                    queue.add(newPath);

                    if (neighbor.equals(target)) {
                        return newPath;
                    }
                }
                visited.add(node);
            }
        }
        return null;
    }

    // ==========================================
    // 2. Dijkstra 演算法
    // ==========================================
    static class NodeRecord {
        String name;
        int distance;
        NodeRecord(String name, int distance) {
            this.name = name;
            this.distance = distance;
        }
    }

    public static Map<String, Integer> dijkstra(Map<String, Map<String, Integer>> graph, String start) {
        Map<String, Integer> distances = new HashMap<>();
        for (String node : graph.keySet()) {
            distances.put(node, Integer.MAX_VALUE);
        }
        distances.put(start, 0);

        PriorityQueue<NodeRecord> pq = new PriorityQueue<>(Comparator.comparingInt(n -> n.distance));
        pq.add(new NodeRecord(start, 0));
        Set<String> done = new HashSet<>();

        while (!pq.isEmpty()) {
            NodeRecord current = pq.poll();

            if (done.contains(current.name)) continue;
            done.add(current.name);

            Map<String, Integer> neighbors = graph.getOrDefault(current.name, Collections.emptyMap());
            for (Map.Entry<String, Integer> neighbor : neighbors.entrySet()) {
                String nextNode = neighbor.getKey();
                int weight = neighbor.getValue();
                
                if (distances.get(current.name) != Integer.MAX_VALUE) {
                    int newDist = distances.get(current.name) + weight;
                    int currentNextDist = distances.getOrDefault(nextNode, Integer.MAX_VALUE);
                    
                    if (newDist < currentNextDist) {
                        distances.put(nextNode, newDist);
                        pq.add(new NodeRecord(nextNode, newDist));
                    }
                }
            }
        }
        return distances;
    }

    // ==========================================
    // 3. Bellman-Ford 演算法
    // ==========================================
    static class Edge {
        String u, v;
        int weight;
        Edge(String u, String v, int weight) {
            this.u = u;
            this.v = v;
            this.weight = weight;
        }
    }

    public static Map<String, Integer> bellmanFord(List<String> vertices, List<Edge> edges, String start) {
        Map<String, Integer> distances = new HashMap<>();
        for (String v : vertices) {
            distances.put(v, Integer.MAX_VALUE);
        }
        distances.put(start, 0);

        for (int i = 0; i < vertices.size() - 1; i++) {
            boolean updated = false;
            for (Edge edge : edges) {
                if (distances.get(edge.u) != Integer.MAX_VALUE && distances.get(edge.u) + edge.weight < distances.get(edge.v)) {
                    distances.put(edge.v, distances.get(edge.u) + edge.weight);
                    updated = true;
                }
            }
            if (!updated) break;
        }

        for (Edge edge : edges) {
            if (distances.get(edge.u) != Integer.MAX_VALUE && distances.get(edge.u) + edge.weight < distances.get(edge.v)) {
                System.out.println("    [警告] 圖中包含負權重循環！無法計算最短路徑。");
                return null;
            }
        }
        return distances;
    }

    // ==========================================
    // 4. 0/1 背包問題 (動態規劃 DP)
    // ==========================================
    public static int knapsack01(int capacity, int[] weights, int[] values, int n) {
        int[][] dp = new int[n + 1][capacity + 1];

        for (int i = 1; i <= n; i++) {
            for (int w = 1; w <= capacity; w++) {
                if (weights[i - 1] <= w) {
                    dp[i][w] = Math.max(
                        dp[i - 1][w], 
                        dp[i - 1][w - weights[i - 1]] + values[i - 1]
                    );
                } else {
                    dp[i][w] = dp[i - 1][w];
                }
            }
        }
        return dp[n][capacity];
    }

    // ==========================================
    // 測試執行區塊 (包含詳細過程與時間複雜度輸出)
    // ==========================================
    public static void main(String[] args) {
        
        // ---------------------------------------------------------
        System.out.println("==================================================");
        System.out.println(" 1. 廣度優先搜尋 (BFS) - 尋找最少節點路徑");
        System.out.println("==================================================");
        System.out.println(" [時間複雜度] O(V + E)  (V: 節點數, E: 邊數)");
        System.out.println(" [執行過程與結果] :");
        
        Map<String, List<String>> socialNetwork = new HashMap<>();
        socialNetwork.put("Marshall", Arrays.asList("James", "Amy", "John", "Uriah"));
        socialNetwork.put("Uriah", Arrays.asList("Marshall", "Andy"));
        socialNetwork.put("Andy", Arrays.asList("Uriah", "May", "Eric", "Bella"));
        socialNetwork.put("May", Arrays.asList("Andy"));
        socialNetwork.put("James", Arrays.asList("Marshall", "Sunny"));
        socialNetwork.put("Amy", Arrays.asList("Marshall", "Sunny"));
        socialNetwork.put("John", Arrays.asList("Marshall", "Cara"));
        socialNetwork.put("Cara", Arrays.asList("John", "Bella"));
        socialNetwork.put("Bella", Arrays.asList("Cara", "Andy", "Eric"));
        socialNetwork.put("Sunny", Arrays.asList("James", "Amy"));
        socialNetwork.put("Eric", Arrays.asList("Andy", "Bella"));
        
        String startPerson = "Marshall";
        String targetPerson = "May";
        System.out.println("    目標: 在人際網路中尋找從 [" + startPerson + "] 到 [" + targetPerson + "] 的最短聯繫路徑。");
        
        List<String> bfsPath = bfsShortestPath(socialNetwork, startPerson, targetPerson);
        if (bfsPath != null) {
            System.out.println("    -> 成功找到路徑！順序為: " + String.join(" -> ", bfsPath));
            System.out.println("    -> 總共經過節點數: " + bfsPath.size() + " (需要透過 " + (bfsPath.size() - 2) + " 個中間人)");
        } else {
            System.out.println("    -> 無法找到連通的路徑。");
        }

        // ---------------------------------------------------------
        System.out.println("\n==================================================");
        System.out.println(" 2. Dijkstra 演算法 - 無負權重的單源最短路徑");
        System.out.println("==================================================");
        System.out.println(" [時間複雜度] O((V + E) log V)  (使用 Priority Queue)");
        System.out.println(" [執行過程與結果] :");
        
        Map<String, Map<String, Integer>> dijkstraGraph = new HashMap<>();
        dijkstraGraph.put("1", new HashMap<String, Integer>() {{ put("2", 4); put("5", 5); }});
        dijkstraGraph.put("2", new HashMap<String, Integer>() {{ put("3", 2); put("4", 7); put("5", 4); }});
        dijkstraGraph.put("3", new HashMap<String, Integer>() {{ put("4", 10); }});
        dijkstraGraph.put("4", new HashMap<String, Integer>() {{ put("6", 8); }});
        dijkstraGraph.put("5", new HashMap<String, Integer>() {{ put("4", 4); put("6", 6); }});
        dijkstraGraph.put("6", new HashMap<>()); 
        
        String dStart = "1";
        System.out.println("    目標: 計算從起點 [" + dStart + "] 到圖中所有其他節點的最短距離。");
        Map<String, Integer> dijkstraRes = dijkstra(dijkstraGraph, dStart);
        
        for (Map.Entry<String, Integer> entry : dijkstraRes.entrySet()) {
            if(entry.getValue() == Integer.MAX_VALUE) {
                System.out.println("    -> 到節點 " + entry.getKey() + " 的最短距離: 無法抵達 (∞)");
            } else {
                System.out.println("    -> 到節點 " + entry.getKey() + " 的最短距離: " + entry.getValue());
            }
        }

        // ---------------------------------------------------------
        System.out.println("\n==================================================");
        System.out.println(" 3. Bellman-Ford 演算法 - 容許負權重的最短路徑");
        System.out.println("==================================================");
        System.out.println(" [時間複雜度] O(V * E)  (需要執行 V-1 輪全圖掃描)");
        System.out.println(" [執行過程與結果] :");
        
        List<String> bfVertices = Arrays.asList("A", "B", "C");
        List<Edge> bfEdges = Arrays.asList(
            new Edge("A", "B", 5),
            new Edge("A", "C", 10),
            new Edge("C", "B", -50) // 負權重
        );
        
        String bfStart = "A";
        System.out.println("    目標: 計算從起點 [" + bfStart + "] 出發的最短距離 (包含負權重邊 C->B: -50)。");
        Map<String, Integer> bfRes = bellmanFord(bfVertices, bfEdges, bfStart);
        
        if (bfRes != null) {
            for (Map.Entry<String, Integer> entry : bfRes.entrySet()) {
                System.out.println("    -> 到節點 " + entry.getKey() + " 的最短距離: " + entry.getValue());
            }
        }

        // ---------------------------------------------------------
        System.out.println("\n==================================================");
        System.out.println(" 4. 0/1 背包問題 (動態規劃 DP)");
        System.out.println("==================================================");
        System.out.println(" [時間複雜度] O(N * W)  (N: 物品數量, W: 背包最大容量)");
        System.out.println(" [執行過程與結果] :");
        
        int[] itemWeights = {10, 20, 30};
        int[] itemValues = {30, 10, 5};
        int maxCapacity = 40;
        int numItems = itemValues.length;
        
        System.out.println("    設定參數:");
        System.out.println("    -> 背包最大容量: " + maxCapacity);
        System.out.println("    -> 物品清單 (重量, 價值): ");
        for(int i=0; i<numItems; i++) {
            System.out.println("       物品 " + (i+1) + ": 重量=" + itemWeights[i] + ", 價值=" + itemValues[i]);
        }
        
        int maxVal = knapsack01(maxCapacity, itemWeights, itemValues, numItems);
        System.out.println("    -> [最佳解] 在不超過容量 " + maxCapacity + " 的情況下，能裝入的最大總價值為: " + maxVal);
        System.out.println("==================================================\n");
    }
}