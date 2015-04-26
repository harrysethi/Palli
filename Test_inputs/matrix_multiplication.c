void foo(){}
void bar(){}


int main()
{
foo();
	int a[100][100], b[100][100], c[100][100];
	int i, j, k;
	for(i = 0; i < 100; i++)
	{
		for(j = 0; j < 100; j++)
		{
			for(k = 0; k < 100; k++)
			{
				c[i][j] = a[i][k] * b[k][j];
			}
		}
	}
bar();

	return 0;
}
