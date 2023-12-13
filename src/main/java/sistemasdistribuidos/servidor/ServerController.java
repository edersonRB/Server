package sistemasdistribuidos.servidor;

import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;
import org.mindrot.jbcrypt.BCrypt;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerController {
    private String SECRET_KEY = "AoT3QFTTEkj16rCby/TPVBWvfSQHL3GeEz3zVwEd6LDrQDT97sgDY8HJyxgnH79jupBWFOQ1+7fRPBLZfpuA2lwwHqTgk+NJcWQnDpHn31CVm63Or5c5gb4H7/eSIdd+7hf3v+0a5qVsnyxkHbcxXquqk9ezxrUe93cFppxH4/kF/kGBBamm3kuUVbdBUY39c4U3NRkzSO+XdGs69ssK5SPzshn01axCJoNXqqj+ytebuMwF8oI9+ZDqj/XsQ1CLnChbsL+HCl68ioTeoYU9PLrO4on+rNHGPI0Cx6HrVse7M3WQBPGzOd1TvRh9eWJrvQrP/hm6kOR7KrWKuyJzrQh7OoDxrweXFH8toXeQRD8=";
    @FXML
    private TextField serverPort;
    private ExecutorService threadPool = Executors.newCachedThreadPool();
    private ArrayList<User> userList = new ArrayList<>();
    private ArrayList<Point> pointList = new ArrayList<>();
    private ArrayList<Segment> segmentList = new ArrayList<>();
    private int idCounter = 0;
    private int pointIdCounter = 22;
    private int segmentIdCounter = 0;
    private boolean acceptingConnections = true;

    @FXML
    protected void onConnectButtonClick() {
        ServerService serverService = new ServerService();

        initialize();

        serverService.start();
    }
    @FXML
    protected void onListUsersClick() {
        StringBuilder messageBuilder = new StringBuilder();

        for (User user : userList) {
            String name = user.getName();
            String email = user.getEmail();
            String token = user.getToken();

            if (!token.isEmpty()) {
                messageBuilder.append(name).append(" - ").append(email).append("\n");
            }
        }

        String message = messageBuilder.toString();

        // Remove the trailing comma and space if present
        if (message.endsWith(", ")) {
            message = message.substring(0, message.length() - 2);
        }

        showWarning(message);
    }


    private void initialize() {
        //123456
        User exampleAdmin = new User(idCounter, "admin 1", "admin", hash("E10ADC3949BA59ABBE56E057F20F883E"), true, "");
        idCounter++;
        User secondAdmin = new User(idCounter, "admin 2", "admin2@email.com", hash("E10ADC3949BA59ABBE56E057F20F883E"), true, "");
        idCounter++;
        User exampleUser = new User(idCounter, "usuario default", "user@email.com", hash("E10ADC3949BA59ABBE56E057F20F883E"), false, "");
        idCounter++;

        userList.add(exampleAdmin);
        userList.add(secondAdmin);
        userList.add(exampleUser);

        pointList.add(new Point(1, "Portaria principal", ""));
        pointList.add(new Point(2, "Escada 1", ""));
        pointList.add(new Point(3, "Capela", ""));
        pointList.add(new Point(4, "Escada 2, baixo", null));
        pointList.add(new Point(5, "Lab 6", ""));
        pointList.add(new Point(6, "Lab 7", ""));
        pointList.add(new Point(7, "Escada 4", ""));
        pointList.add(new Point(8, "Lab 8", ""));
        pointList.add(new Point(9, "LaCa", ""));
        pointList.add(new Point(10, "Rampa 1", ""));
        pointList.add(new Point(11, "Auditório", ""));
        pointList.add(new Point(12, "Escada 3, baixo", null));
        pointList.add(new Point(13, "Escada 2, cima", null));
        pointList.add(new Point(14, "Lab 2", ""));
        pointList.add(new Point(15, "Lab 1", ""));
        pointList.add(new Point(16, "Lab 4", ""));
        pointList.add(new Point(17, "Escada 3, cima", ""));
        pointList.add(new Point(18, "Lab 1", ""));
        pointList.add(new Point(19, "Lab 3", ""));
        pointList.add(new Point(20, "Lab 5", ""));
        pointList.add(new Point(21, "Dainf", ""));

        segmentList.add(new Segment(1, findPointById(pointList, 2), findPointById(pointList, 1), "Frente", 100, "Cuidado! Escada.", false));
        segmentList.add(new Segment(2, findPointById(pointList, 1), findPointById(pointList, 2), "Frente", 100, null, false));
        segmentList.add(new Segment(3, findPointById(pointList, 3), findPointById(pointList, 2), "Frente", 5, null, false));
        segmentList.add(new Segment(4, findPointById(pointList, 2), findPointById(pointList, 3), "Se vier da escada 2: Esquerda. Se vier do Auditório: Direita", 5, "Cuidado! Escada.", false));
        segmentList.add(new Segment(5, findPointById(pointList, 3), findPointById(pointList, 4), "Se vier da Escada 1: Direita. Se vier do Auditório: Frente", 20, "Cuidado! Escada.", false));
        segmentList.add(new Segment(6, findPointById(pointList, 4), findPointById(pointList, 3), "Se vier da Escada 2, cima: Direita. Se vier do Lab 6: Frente", 20, null, false));
        segmentList.add(new Segment(7, findPointById(pointList, 3), findPointById(pointList, 11), "Se vier da Escada 1: Esquerda. Se vier da Escada 2: Frente.", 20, null, false));
        segmentList.add(new Segment(8, findPointById(pointList, 11), findPointById(pointList, 3), "Se vier da Escada 3, baixo: Direita. Se vier da Rampa 1: Esquerda.", 20, null, false));
        segmentList.add(new Segment(9, findPointById(pointList, 4), findPointById(pointList, 5), "Se vier da Capela: Frente. Se vier da Escada 2, cima: Esquerda.", 2, "Cuidado! Escada.", false));
        segmentList.add(new Segment(10, findPointById(pointList, 5), findPointById(pointList, 4), "Direita", 2, "Cuidado! Escada.", false));
        segmentList.add(new Segment(11, findPointById(pointList, 6), findPointById(pointList, 5), "Esquerda", 20, null, true));
        segmentList.add(new Segment(12, findPointById(pointList, 5), findPointById(pointList, 6), "Frente", 20, null, true));
        segmentList.add(new Segment(13, findPointById(pointList, 8), findPointById(pointList, 6), "Frente", 10, null, false));
        segmentList.add(new Segment(14, findPointById(pointList, 6), findPointById(pointList, 8), "Frente", 10, null, false));
        segmentList.add(new Segment(15, findPointById(pointList, 7), findPointById(pointList, 8), "Frente", 5, null, false));
        segmentList.add(new Segment(16, findPointById(pointList, 8), findPointById(pointList, 7), "Direita", 5, null, false));
        segmentList.add(new Segment(17, findPointById(pointList, 9), findPointById(pointList, 7), "Esquerda", 2, null, false));
        segmentList.add(new Segment(18, findPointById(pointList, 7), findPointById(pointList, 9), "Frente", 2, "Cuidado! Escada.", false));
        segmentList.add(new Segment(19, findPointById(pointList, 10), findPointById(pointList, 9), "Frente", 40, "Cuidado! Rampa.", false));
        segmentList.add(new Segment(20, findPointById(pointList, 9), findPointById(pointList, 10), "Direita", 40, null, false));
        segmentList.add(new Segment(21, findPointById(pointList, 11), findPointById(pointList, 10), "Frente", 35, "Cuidado! Mantenha-se a esquerda.", false));
        segmentList.add(new Segment(22, findPointById(pointList, 10), findPointById(pointList, 11), "Se vier da Escada 3: Frente. Se vier da Capela: Direita.", 35, "Cuidado! Rampa.", false));
        segmentList.add(new Segment(23, findPointById(pointList, 12), findPointById(pointList, 11), "Se vier da Capela: Esquerda. Se vier da Rampa 1: Frente.", 2, "Cuidado! Escada.", false));
        segmentList.add(new Segment(24, findPointById(pointList, 17), findPointById(pointList, 12), "Cima", 10, "Cuidado! Escada.", false));
        segmentList.add(new Segment(25, findPointById(pointList, 12), findPointById(pointList, 17), "Baixo", 10, "Cuidado! Escada.", false));
        segmentList.add(new Segment(26, findPointById(pointList, 13), findPointById(pointList, 4), "Cima", 10, "Cuidado! Escada.", false));
        segmentList.add(new Segment(27, findPointById(pointList, 4), findPointById(pointList, 13), "Baixo", 10, "Cuidado! Escada.", false));
        segmentList.add(new Segment(28, findPointById(pointList, 14), findPointById(pointList, 13), "Se vier da Escada 2, baixo: Direita. Se vier de Lab 1: Frente.", 3, null, false));
        segmentList.add(new Segment(29, findPointById(pointList, 13), findPointById(pointList, 14), "Frente", 3, "Cuidado! Escada.", false));
        segmentList.add(new Segment(30, findPointById(pointList, 15), findPointById(pointList, 13), "Se vier da Escada 2, baixo: Esquerda. Se vier do Lab 2: Frente.", 2, null, false));
        segmentList.add(new Segment(31, findPointById(pointList, 19), findPointById(pointList, 15), "Frente", 2, null, false));
        segmentList.add(new Segment(32, findPointById(pointList, 18), findPointById(pointList, 19), "Direita", 2, null, false));
        segmentList.add(new Segment(33, findPointById(pointList, 20), findPointById(pointList, 19), "Esquerda", 10, null, false));
        segmentList.add(new Segment(34, findPointById(pointList, 19), findPointById(pointList, 20), "Frente", 10, null, false));
        segmentList.add(new Segment(35, findPointById(pointList, 21), findPointById(pointList, 20), "Frente", 15, null, false));
        segmentList.add(new Segment(36, findPointById(pointList, 20), findPointById(pointList, 21), "Frente", 15, null, false));
        segmentList.add(new Segment(37, findPointById(pointList, 16), findPointById(pointList, 14), "Frente", 5, null, false));
        segmentList.add(new Segment(38, findPointById(pointList, 14), findPointById(pointList, 16), "Frente", 5, null, false));
        segmentList.add(new Segment(39, findPointById(pointList, 16), findPointById(pointList, 17), "Esquerda", 30, null, false));
        segmentList.add(new Segment(40, findPointById(pointList, 17), findPointById(pointList, 16), "Direita", 30, "Cuidado! Escada.", false));
    }

    private Point findPointById(ArrayList<Point> pointList, int id) {
        for (Point point : pointList) {
            if (point.getId() == id) {
                return point;
            }
        }
        // Handle the case where the point with the specified id is not found
        throw new IllegalArgumentException("Point with id " + id + " not found in the list.");
    }

    private void showWarning(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private String getResponse(String request) {
        JSONObject responseJson = new JSONObject();
        
        try {
            JSONObject requestJson = new JSONObject(request);

            if (requestJson.has("action") && requestJson.getString("action").equals("autocadastro-usuario")) {
                responseJson =  getRegisterResponse(requestJson);
            }
            else if (requestJson.has("action") && requestJson.getString("action").equals("login")) {
                responseJson =  getLoginResponse(requestJson);
            }
            else if (requestJson.has("action") && requestJson.getString("action").equals("logout")) {
                responseJson = getLogoutResponse(requestJson);
            }
            else if (requestJson.has("action") && requestJson.getString("action").equals("pedido-proprio-usuario")) {
                responseJson = getLoggedUserData(requestJson);
            }
            else if (requestJson.has("action") && requestJson.getString("action").equals("listar-usuarios")) {
                responseJson = getListUsers(requestJson);
            }
            else if (requestJson.has("action") && (requestJson.getString("action").equals("autoedicao-usuario") || requestJson.getString("action").equals("edicao-usuario"))) {   	
                responseJson = getChangeUser(requestJson);
            }
            else if (requestJson.has("action") && (requestJson.getString("action").equals("excluir-proprio-usuario") || requestJson.getString("action").equals("excluir-usuario"))) {
                responseJson = getDeleteUser(requestJson);
            }
            else if (requestJson.has("action") && requestJson.getString("action").equals("cadastro-ponto")) {
                responseJson = getRegisterPointResponse(requestJson);
            }
            else if (requestJson.has("action") && requestJson.getString("action").equals("listar-pontos")) {
                responseJson = getListPoints(requestJson);
            }
            else if (requestJson.has("action") && (requestJson.getString("action").equals("excluir-ponto") )) {
                responseJson = getDeletePoint(requestJson);
            }
            else if (requestJson.has("action") && requestJson.getString("action").equals("cadastro-segmento")) {
                responseJson = getRegisterSegmentResponse(requestJson);
            }
            else if (requestJson.has("action") && requestJson.getString("action").equals("listar-segmentos")) {
                responseJson = getListSegments(requestJson);
            }
            else if (requestJson.has("action") && (requestJson.getString("action").equals("excluir-segmento") )) {
                responseJson = getDeleteSegment(requestJson);
            }
            else if (requestJson.has("action") && (requestJson.getString("action").equals("pedido-edicao-segmento") )) {
                responseJson = getRequestEditSegment(requestJson);
            }
            else if (requestJson.has("action") && (requestJson.getString("action").equals("edicao-segmento") )) {
                responseJson = getEditSegment(requestJson);
            }
            else if (requestJson.has("action") && requestJson.getString("action").equals("pedido-rotas")) {
                responseJson = getRoute(requestJson);
            }
            else {
                responseJson.put("action", requestJson.has("action"));
                responseJson.put("error", true);
                responseJson.put("message", "Ação desconhecida");
            }
        }
        catch(JSONException e) {
            System.out.println(e);
        }
        String response = responseJson.toString();
        System.out.println("S-->C: " + response);
        System.out.println("------------------------------------------------------------------");
        return response;
    }

    private JSONObject getListUsers(JSONObject requestJson) {
        JSONObject responseJson = new JSONObject();
        System.out.println("Entrei Lista de usuarios");
        try {
        	
        	JSONArray users = new JSONArray();
        	JSONObject data = new JSONObject();
        	

        	responseJson.put("message", "Sucesso");
            responseJson.put("error", false);
            responseJson.put("action", "listar-usuarios");
        	
        	for(User user: userList) {
        		JSONObject usuario = new JSONObject();
        		usuario.put("id", user.getId());
        		usuario.put("name", user.getName());
        		usuario.put("type", user.getType());
        		usuario.put("email", user.getEmail());
        		users.put(usuario);
        	}
        	responseJson.put("data", data);
        	data.put("users", users);        
        }
        catch(JSONException e) {
            System.out.println(e.toString());
        }
        return responseJson;
    }

    private JSONObject getListPoints(JSONObject requestJson) {
        JSONObject responseJson = new JSONObject();
        try {

            JSONArray pontos = new JSONArray();
            JSONObject data = new JSONObject();

            responseJson.put("message", "Sucesso");
            responseJson.put("error", false);
            responseJson.put("action", "listar-pontos");

            for(Point point: pointList) {
                JSONObject ponto = new JSONObject();
                ponto.put("id", point.getId());
                ponto.put("name", point.getName());
                ponto.put("obs", point.getObs() != null ? point.getObs() : JSONObject.NULL);
                pontos.put(ponto);
            }
            responseJson.put("data", data);
            data.put("pontos", pontos);
        }
        catch(JSONException e) {
            System.out.println(e.toString());
        }
        return responseJson;
    }

    private JSONObject getRoute(JSONObject requestJson) {
        JSONObject responseJson = new JSONObject();
        try {

            JSONArray segmentos = new JSONArray();
            JSONObject data = new JSONObject();

            responseJson.put("message", "Rota recuperada com sucesso");
            responseJson.put("error", false);
//            responseJson.put("action", "pedido-rotas");

            int startPointId = requestJson.getJSONObject("data").getJSONObject("ponto_origem").getInt("id");
            int endPointId = requestJson.getJSONObject("data").getJSONObject("ponto_destino").getInt("id");

            List<Segment> shortestPathSegments = findShortestPath(startPointId, endPointId);

            for(Segment segment: shortestPathSegments) {
                JSONObject segmento = new JSONObject();
                segmento.put("direcao", segment.getDirection());
                segmento.put("distancia", segment.getDistance());
                segmento.put("obs", segment.getObs() != null ? segment.getObs() : JSONObject.NULL);
                segmento.put("bloqueado", segment.isBlocked());

                Point originPoint = segment.getOrigin();
                JSONObject origin = new JSONObject();
                origin.put("id",originPoint.getId());
                origin.put("name",originPoint.getName());
                origin.put("obs",originPoint.getObs() != null ? originPoint.getObs() : JSONObject.NULL);
                segmento.put("ponto_origem",origin);

                Point destinyPoint = segment.getDestiny();
                JSONObject destiny = new JSONObject();
                destiny.put("id",destinyPoint.getId());
                destiny.put("name",destinyPoint.getName());
                destiny.put("obs",destinyPoint.getObs() != null ? destinyPoint.getObs() : JSONObject.NULL);
                segmento.put("ponto_destino",destiny);

                segmentos.put(segmento);
            }
            responseJson.put("segmentos", segmentos);
        }
        catch(JSONException e) {
            System.out.println(e.toString());
        }
        return responseJson;
    }

    // Dijkstra's Algorithm implementation
    private List<Segment> findShortestPath(int startPointId, int endPointId) {
        Map<Integer, Integer> distanceMap = new HashMap<>();
        Map<Integer, Segment> predecessorMap = new HashMap<>();
        PriorityQueue<PointDistance> priorityQueue = new PriorityQueue<>();

        // Initialize distances and predecessors
        for (Point point : pointList) {
            distanceMap.put(point.getId(), Integer.MAX_VALUE);
            predecessorMap.put(point.getId(), null);
        }

        // Set distance to the starting point as 0
        distanceMap.put(startPointId, 0);
        priorityQueue.add(new PointDistance(startPointId, 0));

        while (!priorityQueue.isEmpty()) {
            PointDistance current = priorityQueue.poll();
            int currentPointId = current.getPointId();

            for (Segment neighborSegment : getNeighborSegments(currentPointId)) {
                if (neighborSegment.isBlocked()) {
                    continue;  // Skip blocked segments
                }

                int neighborPointId = neighborSegment.getDestiny().getId();
                int newDistance = distanceMap.get(currentPointId) + neighborSegment.getDistance();

                if (newDistance < distanceMap.get(neighborPointId)) {
                    distanceMap.put(neighborPointId, newDistance);
                    predecessorMap.put(neighborPointId, neighborSegment);
                    priorityQueue.add(new PointDistance(neighborPointId, newDistance));
                }
            }
        }

        // Reconstruct the shortest path
        List<Segment> shortestPathSegments = new ArrayList<>();
        for (Segment segment = predecessorMap.get(endPointId); segment != null; segment = predecessorMap.get(segment.getOrigin().getId())) {
            shortestPathSegments.add(0, segment);
        }

        return shortestPathSegments;
    }

    // Helper class for priority queue
    private static class PointDistance implements Comparable<PointDistance> {
        private final int pointId;
        private final int distance;

        public PointDistance(int pointId, int distance) {
            this.pointId = pointId;
            this.distance = distance;
        }

        public int getPointId() {
            return pointId;
        }

        @Override
        public int compareTo(PointDistance other) {
            return Integer.compare(distance, other.distance);
        }
    }

    // Helper method to get neighboring segments of a point
    private List<Segment> getNeighborSegments(int pointId) {
        List<Segment> neighbors = new ArrayList<>();
        for (Segment segment : segmentList) {
            if (segment.getOrigin().getId() == pointId) {
                neighbors.add(segment);
            }
        }
        return neighbors;
    }

    private JSONObject getListSegments(JSONObject requestJson) {
        JSONObject responseJson = new JSONObject();
        try {

            JSONArray segmentos = new JSONArray();
            JSONObject data = new JSONObject();

            responseJson.put("message", "Sucesso");
            responseJson.put("error", false);
            responseJson.put("action", "listar-segmentos");

            for(Segment segment: segmentList) {
                JSONObject segmento = new JSONObject();
                segmento.put("id", segment.getId());
                System.out.println(segment.getId());
                segmento.put("direcao", segment.getDirection());
                segmento.put("distancia", segment.getDistance());
                segmento.put("obs", segment.getObs() != null ? segment.getObs() : JSONObject.NULL);
                segmento.put("bloqueado", segment.isBlocked());

                Point originPoint = segment.getOrigin();
                JSONObject origin = new JSONObject();
                origin.put("id",originPoint.getId());
                origin.put("name",originPoint.getName());
                origin.put("obs",originPoint.getObs() != null ? originPoint.getObs() : JSONObject.NULL);
                segmento.put("ponto_origem",origin);

                Point destinyPoint = segment.getDestiny();
                JSONObject destiny = new JSONObject();
                destiny.put("id",destinyPoint.getId());
                destiny.put("name",destinyPoint.getName());
                destiny.put("obs",destinyPoint.getObs() != null ? destinyPoint.getObs() : JSONObject.NULL);
                segmento.put("ponto_destino",destiny);

                segmentos.put(segmento);
            }
            data.put("segmentos", segmentos);
            responseJson.put("data", data);
        }
        catch(JSONException e) {
            System.out.println(e.toString());
        }
        return responseJson;
    }

    private JSONObject getRequestEditSegment(JSONObject requestJson) {
        JSONObject responseJson = new JSONObject();
        try {
            int id = requestJson.getJSONObject("data").getInt("segmento_id");

            JSONObject data = new JSONObject();
            responseJson.put("message", "Edição autorizada");
            responseJson.put("error", false);
            responseJson.put("action", "pedido-edicao-segmento");

            for(Segment segment: segmentList) {
                if(segment.getId() == id) {
                    JSONObject segmento = new JSONObject();
                    segmento.put("id", segment.getId());
                    segmento.put("direcao", segment.getDirection());
                    segmento.put("distancia", segment.getDistance());
                    segmento.put("obs", segment.getObs() != null ? segment.getObs() : JSONObject.NULL);
                    segmento.put("bloqueado", segment.isBlocked());

                    Point originPoint = segment.getOrigin();
                    JSONObject origin = new JSONObject();
                    origin.put("id",originPoint.getId());
                    origin.put("name",originPoint.getName());
                    origin.put("obs",originPoint.getObs() != null ? originPoint.getObs() : JSONObject.NULL);
                    segmento.put("ponto_origem",origin);

                    Point destinyPoint = segment.getDestiny();
                    JSONObject destiny = new JSONObject();
                    destiny.put("id",destinyPoint.getId());
                    destiny.put("name",destinyPoint.getName());
                    destiny.put("obs",destinyPoint.getObs() != null ? destinyPoint.getObs() : JSONObject.NULL);
                    segmento.put("ponto_destino",destiny);

                    data.put("segmento",segmento);
                }
            }
            responseJson.put("data", data);
        }
        catch(JSONException e) {
            System.out.println(e.toString());
        }
        return responseJson;
    }

    private JSONObject getEditSegment(JSONObject requestJson) {
        JSONObject responseJson = new JSONObject();
        try {

            JSONObject data = new JSONObject();
            responseJson.put("message", "Segmento editado!");
            responseJson.put("error", false);
            responseJson.put("action", "edicao-segmento");
            int id = requestJson.getJSONObject("data").getJSONObject("segmento").getInt("id");

            for(Segment segment: segmentList) {
                if(segment.getId() == id) {
                    String direction = requestJson.getJSONObject("data").getJSONObject("segmento").getString("direcao");
                    String obs = requestJson.getJSONObject("data").getJSONObject("segmento").getString("obs");
                    boolean blocked = requestJson.getJSONObject("data").getJSONObject("segmento").getBoolean("bloqueado");
                    int distance = requestJson.getJSONObject("data").getJSONObject("segmento").getInt("distancia");

                    int originId = requestJson.getJSONObject("data").getJSONObject("segmento").getJSONObject("ponto_origem").getInt("id");
                    String originName = requestJson.getJSONObject("data").getJSONObject("segmento").getJSONObject("ponto_origem").getString("name");
                    String originObs = requestJson.getJSONObject("data").getJSONObject("segmento").getJSONObject("ponto_origem").getString("obs");
                    Point originPoint = new Point(id,originName,originObs);

                    int destinyId = requestJson.getJSONObject("data").getJSONObject("segmento").getJSONObject("ponto_destino").getInt("id");
                    String destinyName = requestJson.getJSONObject("data").getJSONObject("segmento").getJSONObject("ponto_destino").getString("name");
                    String destinyObs = requestJson.getJSONObject("data").getJSONObject("segmento").getJSONObject("ponto_destino").getString("obs");
                    Point destinyPoint = new Point(id,originName,originObs);

                    segment.setId(id);
                    segment.setDirection(direction);
                    segment.setObs(obs);
                    segment.setOrigin(originPoint);
                    segment.setDestiny(destinyPoint);
                    segment.setBlocked(blocked);
                }
            }
            responseJson.put("data", data);
        }
        catch(JSONException e) {
            System.out.println(e.toString());
        }
        return responseJson;
    }
    
    private JSONObject getChangeUser(JSONObject requestJson) {
    	JSONObject responseJson = new JSONObject();
    	try {
    		
    		int id;
    		JSONObject jsonData = requestJson.getJSONObject("data");
    		
    		if(requestJson.getString("action").equals("edicao-usuario")) {
    			id = Integer.parseInt(jsonData.getString("user_id"));
    			responseJson.put("action",requestJson.getString("action"));
    		} else {
    			id = Integer.parseInt(jsonData.getString("id"));
    			responseJson.put("action",requestJson.getString("action"));
    		}
    		
    		for(User user : userList) {
    			if(user.getId() == id) {
    				if(!jsonData.getString("name").isBlank() && !jsonData.isNull("name")) {
    					user.setName(jsonData.getString("name"));
    				}
    				if(!jsonData.getString("email").isBlank() && !jsonData.isNull("email")) {
    					user.setEmail(jsonData.getString("email"));
    				}
    				if(!jsonData.getString("password").isBlank() && !jsonData.isNull("password")) {
    					String hspw = hash(jsonData.getString("password"));
    					user.setPassword(hspw);
    				}
    				
    				responseJson.put("error", false);
    				responseJson.put("message", "Usuário atualizado com sucesso!");
    				
    				return responseJson;
    			}
    		}
    		
    		responseJson.put("error", true);
			responseJson.put("message", "Usuário não encontrado!");
			
			return responseJson;
    		
    	}
    	catch(JSONException e) {
    		System.out.println(e.toString());
    	}
    	
    	return responseJson;
    }
    
    private JSONObject getDeleteUser(JSONObject requestJson) {
    	JSONObject responseJson = new JSONObject();
    	try {
    		if(requestJson.getString("action").equals("excluir-usuario")) {
    			for(User user : userList) {
    				if(user.getId() == requestJson.getJSONObject("data").getInt("user_id")) {
    					userList.remove(user);

    					responseJson.put("error", false);
    					responseJson.put("message", "Usuário removido com sucesso!");
    					responseJson.put("action", "excluir-usuario");

    					return responseJson;
    				}
    			}
                responseJson.put("error", true);
                responseJson.put("message", "Usuário não encontrado!");
                responseJson.put("action", "excluir-usuario");
            } else {
    			for(User user : userList) {
                    String email = requestJson.getJSONObject("data").getString("email");
                    String password = requestJson.getJSONObject("data").getString("password");
                    String token = requestJson.getJSONObject("data").getString("token");

                    if(user.getEmail().equals(email) && BCrypt.checkpw(password,user.getPassword()))
                    {
                        userList.remove(user);
                        responseJson.put("error", false);
                        responseJson.put("message", "Usuário removido com sucesso!");
                        responseJson.put("action", "excluir-proprio-usuario");
                        return responseJson;
                    }
    			}
                responseJson.put("error", true);
                responseJson.put("message", "Credenciais invalidas!");
                responseJson.put("action", "excluir-proprio-usuario");
            }
    	} catch (JSONException e) {
    		System.out.println(e.toString());
    	}
    	return responseJson;
    }

    private JSONObject getDeletePoint(JSONObject requestJson) {
        JSONObject responseJson = new JSONObject();
        try {
            for(Point point : pointList) {
                if(point.getId() == requestJson.getJSONObject("data").getInt("ponto_id") ) {
                    pointList.remove(point);

                    responseJson.put("error", false);
                    responseJson.put("message", "Ponto removido com sucesso!");
                    responseJson.put("action", "excluir-ponto");

                    return responseJson;
                }
            }
            responseJson.put("error", true);
            responseJson.put("message", "Ponto não encontrado!");
            responseJson.put("action", "excluir-ponto");
        } catch (JSONException e) {
            System.out.println(e.toString());
        }
        return responseJson;
    }

    private JSONObject getDeleteSegment(JSONObject requestJson) {
        JSONObject responseJson = new JSONObject();
        try {
            for(Segment segment : segmentList) {
                if(segment.getId() == requestJson.getJSONObject("data").getInt("segmento_id") ) {
                    segmentList.remove(segment);

                    responseJson.put("error", false);
                    responseJson.put("message", "Segmento removido com sucesso!");
                    responseJson.put("action", "excluir-segmento");

                    return responseJson;
                }
            }
            responseJson.put("error", true);
            responseJson.put("message", "Ponto não encontrado!");
            responseJson.put("action", "excluir-ponto");
        } catch (JSONException e) {
            System.out.println(e.toString());
        }
        return responseJson;
    }

    private JSONObject getLogoutResponse(JSONObject requestJson) {
        JSONObject responseJson = new JSONObject();
        try {
            String token = requestJson.getJSONObject("data").getString("token");
            for(User user : userList) {
                if(user.getToken().equals(token)) {
                    user.setToken("");
                }
            }

            responseJson.put("action", requestJson.has("logout"));
            responseJson.put("error", false);
            responseJson.put("message", "Logout efetuado com sucesso");
        }
        catch(JSONException e) {
            System.out.println(e.toString());
        }
        return responseJson;
    }

    private JSONObject getLoggedUserData(JSONObject requestJson) {
        JSONObject responseJson = new JSONObject();
        try {
            String token = requestJson.getJSONObject("data").getString("token");
            for (User user: userList) {
                if(user.getToken().equals(token)) {
                    JSONObject data = new JSONObject();
                    JSONObject jasonuser = new JSONObject();
                    data.put("user", jasonuser);
                    jasonuser.put("id", user.getId());
                    jasonuser.put("name", user.getName());
                    jasonuser.put("type", user.getType());
                    jasonuser.put("email", user.getEmail());

                    responseJson.put("action", "pedido-proprio-usuario");
                    responseJson.put("error", false);
                    responseJson.put("message", "Sucesso");
                    responseJson.put("data", data);
                    return responseJson;
                }
            }

            responseJson.put("action", "pedido-proprio-usuario");
            responseJson.put("error", true);
            responseJson.put("message", "Sessão falhou!");
        }
        catch (JSONException e) {
            System.out.println(e.toString());
        }
        return responseJson;
    }
    private JSONObject getRegisterResponse(JSONObject requestJson) {
        JSONObject responseJson = new JSONObject();
        try {
            String name = requestJson.getJSONObject("data").getString("name");
            String email = requestJson.getJSONObject("data").getString("email");
            String password = requestJson.getJSONObject("data").getString("password");
            String hashedpw = hash(password);
            int id = idCounter++;
            System.out.println("PW: " + password+ " || HASH: " + hashedpw);

            for (User user : userList) {
                if (user.getEmail().equals(email)) {
                    JSONObject data = new JSONObject();
                    responseJson.put("action", "autocadastro-usuario");
                    responseJson.put("error", true);
                    responseJson.put("message", "email já cadastrado!");

                    return responseJson;
                }
            }

            User newUser = new User(id, name , email, hashedpw, false, generateToken(id,false));
            System.out.println(newUser.toString());
            userList.add(newUser);
            responseJson.put("action", "autocadastro-usuario");
            responseJson.put("error", false);
            responseJson.put("message", "Usuário cadastrado com sucesso");
        }
        catch (JSONException e) {
            System.out.println(e.toString());
        }
        return responseJson;
    }

    private JSONObject getRegisterPointResponse(JSONObject requestJson) {
        JSONObject responseJson = new JSONObject();
        try {
            String name = requestJson.getJSONObject("data").getString("name");
            String obs = requestJson.getJSONObject("data").getString("obs");
            int id = pointIdCounter++;

            Point newPoint = new Point(id, name , obs);
            System.out.println(newPoint.toString());
            pointList.add(newPoint);
            responseJson.put("action", "cadastro-ponto");
            responseJson.put("error", false);
            responseJson.put("message", "Ponto de referência cadastrado com sucesso");
        }
        catch (JSONException e) {
            System.out.println(e.toString());
        }
        return responseJson;
    }
    private JSONObject getRegisterSegmentResponse(JSONObject requestJson) {
        JSONObject responseJson = new JSONObject();
        try {
            String direction = requestJson.getJSONObject("data").getJSONObject("segmento").getString("direcao");
            String obs = requestJson.getJSONObject("data").getJSONObject("segmento").getString("obs");
            boolean blocked = requestJson.getJSONObject("data").getJSONObject("segmento").getBoolean("bloqueado");
            int distance = requestJson.getJSONObject("data").getJSONObject("segmento").getInt("distancia");
            int id = segmentIdCounter++;

            int originId = requestJson.getJSONObject("data").getJSONObject("segmento").getJSONObject("ponto_origem").getInt("id");
            String originName = requestJson.getJSONObject("data").getJSONObject("segmento").getJSONObject("ponto_origem").getString("name");
            String originObs = requestJson.getJSONObject("data").getJSONObject("segmento").getJSONObject("ponto_origem").getString("obs");
            Point originPoint = new Point(id,originName,originObs);

            int destinyId = requestJson.getJSONObject("data").getJSONObject("segmento").getJSONObject("ponto_destino").getInt("id");
            String destinyName = requestJson.getJSONObject("data").getJSONObject("segmento").getJSONObject("ponto_destino").getString("name");
            String destinyObs = requestJson.getJSONObject("data").getJSONObject("segmento").getJSONObject("ponto_destino").getString("obs");
            Point destinyPoint = new Point(id,originName,originObs);

            Segment newSegment = new Segment(id, originPoint, destinyPoint, direction, distance, obs, blocked);

            System.out.println(newSegment.toString());
            segmentList.add(newSegment);
            responseJson.put("action", "cadastro-segmento");
            responseJson.put("error", false);
            responseJson.put("message", "Segmento cadastrado com sucesso");
        }
        catch (JSONException e) {
            System.out.println(e.toString());
        }
        return responseJson;
    }
    
    private JSONObject getLoginResponse(JSONObject requestJson) {
        JSONObject responseJson = new JSONObject();
        try {
            String email = requestJson.getJSONObject("data").getString("email");
            String password = requestJson.getJSONObject("data").getString("password");

            for (User user : userList) {
                if (user.getEmail().equals(email) && BCrypt.checkpw(password,user.getPassword()) ) {
                    user.setToken(generateToken(user.getId(),user.isAdmin()));
                    JSONObject data = new JSONObject();
                    data.put("token", user.getToken());

                    responseJson.put("action", "login");
                    responseJson.put("error", false);
                    responseJson.put("message", "logado com sucesso");
                    responseJson.put("data", data);

                    return responseJson;
                }
            }

            responseJson.put("action", "login");
            responseJson.put("error", true);
            responseJson.put("message", "Credenciais incorretas");
        }
        catch (JSONException e) {
            System.out.println(e.toString());
        }
        return responseJson;
    }

    public String hash(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public String generateToken(int id, boolean isAdmin) {
        return Jwts.builder()
                .claim("user_id", id)
                .claim("admin", isAdmin)
                .setSubject(Integer.toString(id))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    private class ServerService extends Service<Void> {
        @Override
        protected Task<Void> createTask() {
            return new Task<Void>() {
                @Override
                protected Void call() {
                    int port = Integer.parseInt(serverPort.getText());
                    try (ServerSocket serverSocket = new ServerSocket(port)) {
                        System.out.println("Executando na porta " + port);

                        while (acceptingConnections) {
                            Socket clientSocket = serverSocket.accept();
                            System.out.println("Cliente conectado");

                            threadPool.execute(() -> {
                                try {
                                    InputStream in = clientSocket.getInputStream();
                                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                                    String message;
                                    while ((message = reader.readLine()) != null) {
                                        System.out.println("C-->S: " + message);

                                        String response = getResponse(message);

                                        PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);
                                        writer.println(response);

                                        if (message.equals("EOF")) {
                                            break;
                                        }
                                    }

                                    clientSocket.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            });
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            };
        }
    }

    @FXML
    protected void onStopButtonClick() {
        acceptingConnections = false;
    }
}
