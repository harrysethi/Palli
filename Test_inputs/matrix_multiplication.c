void foo(){}
void bar(){}


int main()
{
foo();
	int a[30][30], b[30][30], c[30][30];
	int i, j, k;
	for(i = 0; i < 30; i++)
	{
		for(j = 0; j < 30; j++)
		{
			for(k = 0; k < 30; k++)
			{
				c[i][j] = a[i][k] * b[k][j];
			}
		}
	}
bar();

	return 0;
}
