package practice.tixi.unionfind;

/**
 * implementation using Array: based on a given m*n matrix
 * 将二维数组r行, l列的元素, 映射到一维数组的 r*col + l
 */
public class UnionFind {
    public int[] parent; // the parent the node belongs to
    public int[] size; // how many nodes in the set
    public int[] help; // used to store the path and then path compression
    public int sets; // count of the set

    // count of col of the given matrix
    int col;

    // constructor method to initialize the one-array
    public UnionFind(int a) {
        // 代表节点的集合
        parent = new int[a];
        // 节点所在的集合大小, 只对代表节点有意义
        size = new int[a];
        for (int i = 0; i < a; i++) {
            parent[i] = i; //初始状态: 自己在自己的集合
        }
    }

    // 将元素r1,c1和元素r2,c2所在的集合合并
    public void union(int r1, int c1, int r2, int c2) {
        int f1 = find(r1, c1);
        int f2 = find(r2, c2);

        if (f1 != f2) { // union
            if (size[f1] > size[f2]) { // path compression
                parent[f2] = f1;
                size[f1] += size[f2];
                size[f2] = 0;
            } else {
                parent[f1] = f2;
                size[f1] = 0;
                size[f2]++;
            }
            sets--;
        }
    }

    // 找到元素(i,j)的父节点, 即其所在集合的代表节点
    public int find(int i, int j) {
        int index = i + col * j;
        int hi = 0;
        while (parent[index] != index) {
            help[hi++] = index;
            index = parent[index];
        }
        for (hi--; hi >= 0; hi--) { // path compression
            parent[help[hi]] = index;
        }
        return index;
    }
}
