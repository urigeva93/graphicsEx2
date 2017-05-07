
public class Matrix {
	double[][] mat;

	// creating an empty 4x4 mat to represent 3 dim vectors with homogenic
	// cordinates
	public Matrix() {
		this.mat = new double[4][4];
	}

	public Matrix(int dim, double deg) {

		deg = Math.toRadians(deg);
		this.mat = new double[4][4];

		// filling main diagonal with ones
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				if (i == j)
					this.mat[i][j] = 1;
			}
		}
		
		//filling mat with roatation values respectively to dim
		if (dim == 1) {
			this.mat[1][1] = Math.cos(deg);
			this.mat[1][2] = -Math.sin(deg);
			this.mat[2][1] = Math.sin(deg);
			this.mat[2][2] = Math.cos(deg);
		} else if (dim == 2) {
			this.mat[0][0] = Math.cos(deg);
			this.mat[0][2] = Math.sin(deg);
			this.mat[2][0] = -Math.sin(deg);
			this.mat[2][2] = Math.cos(deg);
		} else if (dim == 3) {
			this.mat[0][0] = Math.cos(deg);
			this.mat[0][1] = -Math.sin(deg);
			this.mat[1][0] = Math.sin(deg);
			this.mat[1][1] = Math.cos(deg);
		}
	}

}
