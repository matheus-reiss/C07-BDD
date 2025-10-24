package com.br.inatel.model;

public class TreinoExercicio {
    private Treino treino;
    private Exercicio exercicio;

    private short ordem;
    private short series;
    private short reps;
    private Integer cargaKg;
    private short descansoSeg;

    public Treino getTreino() {
        return treino;
    }

    public void setTreino(Treino treino) {
        this.treino = treino;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public short getOrdem() {
        return ordem;
    }

    public void setOrdem(short ordem) {
        this.ordem = ordem;
    }

    public short getSeries() {
        return series;
    }

    public void setSeries(short series) {
        this.series = series;
    }

    public short getReps() {
        return reps;
    }

    public void setReps(short reps) {
        this.reps = reps;
    }

    public Integer getCargaKg() {
        return cargaKg;
    }

    public void setCargaKg(Integer cargaKg) {
        this.cargaKg = cargaKg;
    }

    public short getDescansoSeg() {
        return descansoSeg;
    }

    public void setDescansoSeg(short descansoSeg) {
        this.descansoSeg = descansoSeg;
    }
}
