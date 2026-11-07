// COSC6025036: Data Structure and Algorithm Analysis
// Final Exam

// Faiza Shifa Medina
// 2902826034

// --------------------------------------------------------
// 3b. Implementasikan graph sederhana berupa adjacency list berbasis array yang merepresentasikan
// persimpangan jalan, dengan ketentuan berikut: (i) sediakan method bfsCariTerdekat(int asal, boolean[] adalahDriver) yang
// mengembalikan simpul driver terdekat, dalam jumlah hop, dari titik asal menggunakan BFS; (ii) implementasikan sendiri dari
// nol menggunakan array biasa, TIDAK BOLEH menggunakan java.util.HashMap, ArrayList, LinkedList, HashSet, atau Queue
// secara langsung; queue BFS dan status "sudah dikunjungi" harus Anda buat sendiri dengan array; dan (iii) buat simulasi di
// main() dengan graph kecil, minimal 8 simpul, dan beberapa driver tersebar, lalu tunjukkan hasil pencarian driver terdekat dari 2
// titik penumpang berbeda.
// --------------------------------------------------------

// Simulasi pencarian driver terdekat (GoCepat) dengan BFS

class AntrianInt {
    private final int[] data;
    private int depan = 0; // indeks untuk elemen yang akan keluar berikutnya
    private int belakang; // indeks untuk tempat elemen baru akan masuk

    AntrianInt(int kapasitas){
        data = new int[kapasitas];
    }

    boolean kosong(){
        return depan == belakang;
    }

    void masuk(int nilai) {
        if(belakang == data.length){
            throw new IllegalStateException("Antrian penuh");
        }
        data[belakang] = nilai;
        belakang++;
    }

    int keluar(){
        if(kosong()){
            throw new IllegalStateException("Antrian kosong");
        }
        int nilai = data[depan];
        depan++;
        return nilai;
    }
}

// Unweighted graph tidak berarah berbasis adjacency list
// Daftar tetangga tiap simpul --> Array int yang bisa membesar sendiri

class GrafPersimpangan{
    private static final int KAPASITAS_AWAL = 2;

    private final int jumlahSimpul;
    private final int[][] tetangga; //tetangga[u] --> Array daftar tetangga simpul u
    private final int[] derajat; //derajat[u] --> jumlah tetangga[u] yang sudah terpakai

    GrafPersimpangan(int jumlahSimpul){
        if(jumlahSimpul <= 0){
            throw new IllegalArgumentException("Jumlah simpul harus > 0");
        }
        this.jumlahSimpul = jumlahSimpul;
        this.tetangga = new int[jumlahSimpul][KAPASITAS_AWAL];
        this.derajat = new int[jumlahSimpul];
    }

    int getJumlahSimpul(){
        return jumlahSimpul;
    }

    //Menambah ruas jalan dua arah antara simpul u dan juga v
    void tambahJalan(int u, int v) {
        periksaSimpul(u);
        periksaSimpul(v);
        if(u == v){
            throw new IllegalArgumentException("Jalan ke persimpangan yang sama tidak diizinkan");
        }
        tambahTetangga(u, v);
        tambahTetangga(v, u); //graph tidak berarah --> dicatat di kedua sisi
    }

    //Menambah v ke daftar tetangga u
    private void tambahTetangga(int u, int v){
        if(derajat[u] == tetangga[u].length){
            int[] lebihBesar = new int[tetangga[u].length * 2];
            for(int i = 0; i < derajat[u]; i++){
                lebihBesar[i] = tetangga[u][i];
            }
            tetangga[u] = lebihBesar;
        }
        tetangga[u][derajat[u]] = v;
        derajat[u]++;
    }

    private void periksaSimpul(int u){
        if(u < 0 || u >= jumlahSimpul){
            throw new IllegalArgumentException("Simpul di luar jangkauan: " + u);
        }
    }

    // BFS mulai dari asal, kemudian mengambalikan {simpulDriver, jumlahHop} atau {-1,-1} kalau tidak ada driver yang terjangkau
    private int[] bfs(int asal, boolean[] adalahDriver){
        periksaSimpul(asal);
        if(adalahDriver == null || adalahDriver.length != jumlahSimpul){
            throw new IllegalArgumentException("Array driver harus berukuran sama dengan jumlah simpul");
        }

        boolean[] dikunjungi = new boolean[jumlahSimpul];
        int[] hop = new int[jumlahSimpul];
        AntrianInt antrian = new AntrianInt(jumlahSimpul);

        dikunjungi[asal] = true;
        hop[asal] = 0;
        antrian.masuk(asal);

        while(!antrian.kosong()){
            int u = antrian.keluar();
            if(adalahDriver[u]){
                return new int[] {u, hop[u]};
            }
            for(int i = 0; i < derajat[u]; i++){
                int v = tetangga[u][i];
                if(!dikunjungi[v]){
                    dikunjungi[v] = true; // tandai saat masuk antrian
                    hop[v] = hop[u] + 1;
                    antrian.masuk(v);
                }
            }
        }
        return new int[] {-1, -1};
    }

    //Mengembalikan simpul driver terdekat atau -1 kalau tidak ada
    int bfsCariTerdekat(int asal, boolean[] adalahDriver){
        return bfs(asal, adalahDriver)[0];
    }

    //Mengembalikan jumlah hop ke driver tersekat atau -1 jika tidak ada
    int jarakHopTerdekat(int asal, boolean[] adalahDriver){
        return bfs(asal, adalahDriver)[1];
    }

    void cetakDaftarTetangga(){
        for(int u = 0; u < jumlahSimpul; u++){
            StringBuilder sb = new StringBuilder();
            sb.append(u).append(" -> [");
            for(int i = 0; i < derajat[u]; i++){
                if(i > 0){
                    sb.append(", ");
                }
                sb.append(tetangga[u][i]);
            }
            sb.append("]");
            System.out.println(sb);
        }
    }
}

public class Simulasi{
    public static void main(String[] args){
        GrafPersimpangan peta = new GrafPersimpangan(10);

        peta.tambahJalan(0, 1);
        peta.tambahJalan(0, 2);
        peta.tambahJalan(1, 3);
        peta.tambahJalan(2, 3);
        peta.tambahJalan(3, 4);
        peta.tambahJalan(4, 5);
        peta.tambahJalan(5, 6);
        peta.tambahJalan(6, 7);
        peta.tambahJalan(2, 8);
        peta.tambahJalan(8, 9);

        boolean[] adalahDriver = new boolean[10];
        adalahDriver[5] = true;
        adalahDriver[7] = true;
        adalahDriver[9] = true;

        System.out.println("=======[SIMULASI GO CEPAT]=======");
        System.out.println("Daftar tetangga tiap persimpang:");
        peta.cetakDaftarTetangga();
        System.out.println("Driver ada di simpul: 5, 7, 9");
        System.out.println();

        cetakHasil(peta, 0, adalahDriver);
        cetakHasil(peta, 3, adalahDriver);

        System.out.println();
        System.out.println("-------- CASE TEST -------- ");
        cetakHasil(peta, 7, adalahDriver); //penumpangnya sudah ada di simpul driver
        cetakHasil(peta, 0, new boolean[10]); //tidak ada driver sama sekali
    }

    private static void cetakHasil(GrafPersimpangan peta, int asal, boolean[] adalahDriver){
        int driver = peta.bfsCariTerdekat(asal, adalahDriver);
        if(driver == -1){
            System.out.println("Penumpang di simpul " + asal + " -> Tidak ada driver yang terjangkau");
        } else {
            int hop = peta.jarakHopTerdekat(asal, adalahDriver);
            System.out.println("Penumpang di simpul " + asal + " -> Driver terdekat di simpul " + driver + " (" + hop + " hop)");
        }
    }
}